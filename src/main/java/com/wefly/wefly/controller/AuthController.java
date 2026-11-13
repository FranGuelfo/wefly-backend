package com.wefly.wefly.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.wefly.wefly.model.User;
import com.wefly.wefly.model.dto.security.AuthResponse;
import com.wefly.wefly.model.dto.security.LoginRequest;
import com.wefly.wefly.model.dto.security.RegisterRequest;
import com.wefly.wefly.model.dto.security.TokenRequest;
import com.wefly.wefly.repository.UserRepository;
import com.wefly.wefly.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Tag(name = "1. Autenticación", description = "Endpoints de registro, inicio de sesión tradicional y autenticación social con Google")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // 1. ENDPOINT DE REGISTRO TRADICIONAL
    @Operation(
            summary = "Registro de usuario tradicional",
            description = "Crea un nuevo usuario en la plataforma con email y contraseña, devolviendo el perfil creado y el token JWT de sesión."
    )
    @ApiResponse(responseCode = "201", description = "Usuario registrado y autenticado correctamente")
    @ApiResponse(responseCode = "400", description = "El correo electrónico ya se encuentra registrado", content = @Content)
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        // Comprobar si el email ya existe
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo electrónico ya está registrado");
        }

        // Crear el nuevo usuario encriptando la contraseña con BCrypt
        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setBio("¡Hola! Acabo de unirme a WeFly.");
        newUser.setProfilePictureUrl("");

        User savedUser = userRepository.save(newUser);

        // Generamos el token automáticamente para sesión directa
        String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new AuthResponse(token, savedUser.getId(), savedUser.getName(), savedUser.getEmail())
        );
    }

    // 2. ENDPOINT DE LOGIN TRADICIONAL
    @Operation(
            summary = "Inicio de sesión con email y contraseña",
            description = "Valida las credenciales ingresadas y emite un token JWT de acceso válido por 24 horas."
    )
    @ApiResponse(responseCode = "200", description = "Autenticación exitosa")
    @ApiResponse(responseCode = "401", description = "Credenciales de acceso inválidas", content = @Content)
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));

        // Comparamos la contraseña recibida con el hash almacenado
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        // Generar token JWT
        String token = jwtUtil.generateToken(user.getEmail(), user.getId());

        return ResponseEntity.ok(new AuthResponse(token, user.getId(), user.getName(), user.getEmail()));
    }

    // 3. ENDPOINT PARA LOGIN / REGISTRO CON GOOGLE
    @Operation(
            summary = "Autenticación con Google OAuth2",
            description = "Verifica el token idToken entregado por Google. Si el usuario no existe, se registra automáticamente en el sistema."
    )
    @ApiResponse(responseCode = "200", description = "Token verificado y sesión iniciada")
    @ApiResponse(responseCode = "401", description = "Token de Google inválido o expirado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno al verificar la firma del token con Google", content = @Content)
    @PostMapping("/google")
    public ResponseEntity<AuthResponse> googleLogin(@Valid @RequestBody TokenRequest tokenRequest) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    new GsonFactory()
            ).build();

            GoogleIdToken idToken = verifier.verify(tokenRequest.getIdToken());

            if (idToken == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token de Google inválido o expirado");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");

            Optional<User> userOpt = userRepository.findByEmail(email);
            User user;

            if (userOpt.isEmpty()) {
                // REGISTRO AUTOMÁTICO
                user = new User();
                user.setName(name != null ? name : "Usuario de Google");
                user.setEmail(email);
                user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                user.setProfilePictureUrl(pictureUrl != null ? pictureUrl : "");
                user.setBio("¡Hola! Me he unido a WeFly usando mi cuenta de Google.");

                user = userRepository.save(user);
            } else {
                // LOGIN AUTOMÁTICO Y ACTUALIZACIÓN DE FOTO
                user = userOpt.get();
                if (pictureUrl != null && !pictureUrl.isEmpty()) {
                    user.setProfilePictureUrl(pictureUrl);
                    userRepository.save(user);
                }
            }

            String jwtToken = jwtUtil.generateToken(user.getEmail(), user.getId());

            return ResponseEntity.ok(new AuthResponse(jwtToken, user.getId(), user.getName(), user.getEmail()));

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al procesar la autenticación con Google: " + e.getMessage()
            );
        }
    }
}
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
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
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // Comprobar si el email ya existe
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El correo electrónico ya está registrado");
        }

        // Crear el nuevo usuario encriptando la contraseña con BCrypt
        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        // Puedes setear valores por defecto si los tienes en tu modelo
        newUser.setBio("¡Hola! Acabo de unirme a WeFly.");
        newUser.setProfilePictureUrl("");

        User savedUser = userRepository.save(newUser);

        // Generamos el token automáticamente para que entre directo
        String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new AuthResponse(token, savedUser.getId(), savedUser.getName(), savedUser.getEmail())
        );
    }

    // 2. ENDPOINT DE LOGIN TRADICIONAL
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }

        User user = userOpt.get();

        // Comparamos la contraseña en texto plano recibida con el hash de la base de datos
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }

        // Si es correcta, generamos su token JWT
        String token = jwtUtil.generateToken(user.getEmail(), user.getId());

        return ResponseEntity.ok(new AuthResponse(token, user.getId(), user.getName(), user.getEmail()));
    }

    // 3. ENDPOINT PARA LOGIN / REGISTRO CON GOOGLE
    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody TokenRequest tokenRequest) {
        try {
            // Configuración del verificador de Google
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    new GsonFactory()
            )
                    // Opcional: Aquí podrías restringir por tu CLIENT_ID de Google si lo deseas en el futuro:
                    // .setAudience(Collections.singletonList("TU_CLIENT_ID_DE_GOOGLE.apps.googleusercontent.com"))
                    .build();

            // Validamos el token recibido del frontend
            GoogleIdToken idToken = verifier.verify(tokenRequest.getIdToken());

            if (idToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token de Google inválido o expirado");
            }

            // Si es válido, extraemos los datos del perfil que nos da Google
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");

            // Buscar si el usuario ya existe en nuestro sistema de WeFly
            Optional<User> userOpt = userRepository.findByEmail(email);
            User user;

            if (userOpt.isEmpty()) {
                // --- REGISTRO AUTOMÁTICO (Si es la primera vez que entra) ---
                user = new User();
                user.setName(name != null ? name : "Usuario de Google");
                user.setEmail(email);
                // Al ser cuenta social no usará contraseña tradicional, le generamos un hash aleatorio e inaccesible
                user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                user.setProfilePictureUrl(pictureUrl != null ? pictureUrl : "");
                user.setBio("¡Hola! Me he unido a WeFly usando mi cuenta de Google.");

                user = userRepository.save(user);
            } else {
                // --- LOGIN AUTOMÁTICO (Si ya existía) ---
                user = userOpt.get();
                // Opcional: Actualizar su foto si ha cambiado en Google
                if (pictureUrl != null && !pictureUrl.isEmpty()) {
                    user.setProfilePictureUrl(pictureUrl);
                    userRepository.save(user);
                }
            }

            // Generamos el JWT firmado por WeFly para este usuario
            String jwtToken = jwtUtil.generateToken(user.getEmail(), user.getId());

            // Devolvemos la respuesta idéntica que el login clásico
            return ResponseEntity.ok(new AuthResponse(jwtToken, user.getId(), user.getName(), user.getEmail()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar la autenticación con Google: " + e.getMessage());
        }
    }
}
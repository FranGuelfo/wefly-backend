package com.wefly.wefly.controller;

import com.wefly.wefly.model.Flight;
import com.wefly.wefly.model.dto.JoinFlightRequestDTO;
import com.wefly.wefly.model.dto.MessageResponseDTO;
import com.wefly.wefly.service.BookingService;
import com.wefly.wefly.service.FlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Tag(name = "2. Vuelos", description = "Gestión de vuelos y solicitudes de plaza")
@RestController
@RequestMapping("/api/v1/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;
    private final BookingService bookingService;

    @Operation(summary = "Verificar existencia de un vuelo")
    @ApiResponse(responseCode = "200", description = "Vuelo encontrado")
    @ApiResponse(responseCode = "404", description = "Vuelo no encontrado", content = @Content)
    @GetMapping("/{flightNumber}")
    public ResponseEntity<Flight> verifyFlight(@PathVariable String flightNumber) {
        Optional<Flight> flightOpt = flightService.getOrFetchFlight(flightNumber.toUpperCase().trim());

        return flightOpt
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El número de vuelo no es válido o no opera hoy"));
    }

    @Operation(summary = "Unirse a un vuelo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud de plaza enviada con éxito"),
            @ApiResponse(responseCode = "404", description = "Vuelo no encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Solicitud de plaza inválida", content = @Content)
    })
    @PostMapping("/join")
    public ResponseEntity<MessageResponseDTO> joinFlight(@Valid @RequestBody JoinFlightRequestDTO request) {
        return ResponseEntity.ok(bookingService.joinFlight(request));
    }
}
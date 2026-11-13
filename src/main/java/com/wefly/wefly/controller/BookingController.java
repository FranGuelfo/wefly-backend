package com.wefly.wefly.controller;

import com.wefly.wefly.model.dto.BookingResponseDTO;
import com.wefly.wefly.model.dto.MessageResponseDTO;
import com.wefly.wefly.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "3. Reservas", description = "Gestión de solicitudes de plaza, aprobaciones y rechazos")
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @Operation(summary = "Consultar solicitudes pendientes")
    @ApiResponse(responseCode = "200", description = "Lista recuperada con éxito")
    @ApiResponse(responseCode = "404", description = "Vuelo no encontrado", content = @Content)
    @GetMapping("/pending/{flightId}")
    public ResponseEntity<List<BookingResponseDTO>> getPendingBookings(
            @Parameter(description = "ID del vuelo a consultar", example = "1")
            @PathVariable Long flightId) {
        return ResponseEntity.ok(bookingService.getPendingBookings(flightId));
    }

    @Operation(summary = "Consultar solicitudes confirmadas")
    @ApiResponse(responseCode = "200", description = "Lista recuperada con éxito")
    @ApiResponse(responseCode = "404", description = "Vuelo no encontrado", content = @Content)
    @GetMapping("/confirmed/{flightId}")
    public ResponseEntity<List<BookingResponseDTO>> getConfirmedBookings(@PathVariable Long flightId) {
        return ResponseEntity.ok(bookingService.getConfirmedBookings(flightId));
    }

    @Operation(summary = "Aprobar una solicitud de plaza")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva aprobada con éxito"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada", content = @Content),
            @ApiResponse(responseCode = "400", description = "No hay plazas disponibles", content = @Content)
    })
    @PostMapping("/approve/{bookingId}")
    public ResponseEntity<MessageResponseDTO> approveBooking(
            @Parameter(description = "ID de la reserva a aprobar", example = "1")
            @PathVariable Long bookingId) {
        bookingService.approveBooking(bookingId);
        return ResponseEntity.ok(new MessageResponseDTO(true, "Reserva aprobada y plaza restada"));
    }

    @Operation(summary = "Rechazar una solicitud de plaza")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva rechazada con éxito"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada", content = @Content)
    })
    @DeleteMapping("/reject/{bookingId}")
    public ResponseEntity<MessageResponseDTO> rejectBooking(@PathVariable Long bookingId) {
        bookingService.rejectBooking(bookingId);
        return ResponseEntity.ok(new MessageResponseDTO(true, "Reserva rechazada"));
    }
}
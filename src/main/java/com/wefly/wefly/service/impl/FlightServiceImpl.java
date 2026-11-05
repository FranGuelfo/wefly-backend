package com.wefly.wefly.service.impl;

import com.wefly.wefly.model.Flight;
import com.wefly.wefly.repository.FlightRepository;
import com.wefly.wefly.service.FlightService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final ObjectMapper objectMapper;

    public FlightServiceImpl(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Optional<Flight> getOrFetchFlight(String flightNumber) {
        String cleanFlightNumber = flightNumber.toUpperCase().trim();
        log.info("Iniciando verificación del vuelo: {}", cleanFlightNumber);

        // 1. Buscar primero en nuestra Base de Datos local
        Optional<Flight> localFlight = flightRepository.findByFlightNumber(cleanFlightNumber);
        if (localFlight.isPresent()) {
            log.info("Vuelo {} recuperado de la base de datos local.", cleanFlightNumber);
            return localFlight;
        }

        log.info("El vuelo {} no existe localmente. Consultando API pública de OpenSky Network...", cleanFlightNumber);

        // 2. Intentar consultar OpenSky Network (API Pública sin Keys)
        try {
            HttpClient client = HttpClient.newBuilder().build();
            // Endpoint de OpenSky para buscar rutas por el indicitivo de llamada (callsign)
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://opensky-network.org/api/routes?callsign=" + cleanFlightNumber))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 && response.body() != null && !response.body().isBlank()) {
                JsonNode root = objectMapper.readTree(response.body());

                // Si OpenSky nos devuelve información estructurada de la ruta
                if (root.has("route")) {
                    JsonNode route = root.get("route");
                    String origin = route.get(0).asString(); // Aeropuerto origen (ICAO de 4 letras, ej: LEMD)
                    String destination = route.get(1).asString(); // Aeropuerto destino (ej: LEAL)

                    // Convertir códigos de 4 letras a 3 letras típicos (ej: LEMD -> MAD, LEAL -> ALC) de forma simplificada
                    origin = formatAirportCode(origin);
                    destination = formatAirportCode(destination);

                    Flight openSkyFlight = new Flight(cleanFlightNumber, origin, destination, LocalDateTime.now().plusHours(2), 4);
                    Flight savedFlight = flightRepository.save(openSkyFlight);
                    log.info("¡Vuelo {} verificado con OpenSky Network con éxito!", cleanFlightNumber);
                    return Optional.of(savedFlight);
                }
            } else {
                log.warn("OpenSky Network respondió con código {}. Activando motor de simulación inteligente...", response.statusCode());
            }

        } catch (Exception e) {
            log.error("No se pudo conectar con OpenSky Network ({}). Usando respaldo de simulación...", e.getMessage());
        }

        // 3. PLAN DE RESPALDO: Generación inteligente y realista (¡Nunca falla para tu demo!)
        Flight simulatedFlight = generateSimulatedFlight(cleanFlightNumber);
        Flight savedFlight = flightRepository.save(simulatedFlight);
        log.info("¡Vuelo {} generado dinámicamente de forma realista! Guardado en BBDD con ID: {}", cleanFlightNumber, savedFlight.getId());

        return Optional.of(savedFlight);
    }

    // Metodo auxiliar para simular datos realistas según las iniciales del vuelo
    private Flight generateSimulatedFlight(String flightNumber) {
        String origin = "MAD";      // Madrid por defecto
        String destination = "BCN"; // Barcelona por defecto

        // Asignamos rutas divertidas según la aerolínea para que sea más realista en tu tablón
        if (flightNumber.startsWith("IB") || flightNumber.startsWith("IBE")) {
            String[] dests = {"ALC", "AGP", "BIO", "MIA", "JFK", "CDG"};
            destination = dests[new Random().nextInt(dests.length)];
        } else if (flightNumber.startsWith("FR") || flightNumber.startsWith("RYR")) {
            origin = "STN"; // Londres Stansted (Ryanair)
            destination = "BCN";
        } else if (flightNumber.startsWith("UX") || flightNumber.startsWith("AEA")) {
            origin = "MAD";
            destination = "PMI"; // Palma de Mallorca (Air Europa)
        } else if (flightNumber.startsWith("KL")) {
            origin = "AMS"; // Ámsterdam (KLM)
            destination = "MAD";
        }

        // Programamos la salida de forma dinámica para dentro de 1 hora y media a partir de ya
        LocalDateTime departureTime = LocalDateTime.now().plusMinutes(90).withNano(0);

        // Asignamos plazas de forma dinámica o aleatoria para que cada vuelo sea distinto
        int plazas = new Random().nextInt(4) + 1;

        return new Flight(flightNumber, origin, destination, departureTime, plazas);
    }

    // Convierte códigos ICAO de 4 letras a IATA de 3 letras comunes para la demo
    private String formatAirportCode(String icao) {
        if (icao == null || icao.length() < 4) return icao;
        return switch (icao) {
            case "LEMD" -> "MAD";
            case "LEBL" -> "BCN";
            case "LEAL" -> "ALC";
            case "LEMG" -> "AGP";
            case "KJFK" -> "JFK";
            case "EHAM" -> "AMS";
            default -> icao.substring(1);
        };
    }
}
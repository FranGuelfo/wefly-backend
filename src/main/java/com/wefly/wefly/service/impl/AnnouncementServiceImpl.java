package com.wefly.wefly.service.impl;

import com.wefly.wefly.mapper.AnnouncementMapper;
import com.wefly.wefly.model.Announcement;
import com.wefly.wefly.model.Flight;
import com.wefly.wefly.model.User;
import com.wefly.wefly.model.dto.AnnouncementDTO;
import com.wefly.wefly.repository.AnnouncementRepository;
import com.wefly.wefly.repository.BookingRepository;
import com.wefly.wefly.repository.FlightRepository;
import com.wefly.wefly.repository.UserRepository;
import com.wefly.wefly.service.AnnouncementService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository repository;
    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;
    private final AnnouncementMapper mapper;

    @Override
    public List<AnnouncementDTO> getAnnouncementsByFlight(String flightNumber, Long userId) {
        // 1. Verificación de seguridad: ¿Tiene el usuario una reserva confirmada?
        bookingRepository.findByUserId(userId).stream()
                .filter(b -> b.getFlight().getFlightNumber().equalsIgnoreCase(flightNumber) && b.getIsConfirmed())
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Verifica tu vuelo primero"));

        // 2. Búsqueda por relación: Announcement -> Flight -> flightNumber
        return repository.findByFlight_FlightNumberIgnoreCase(flightNumber)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public AnnouncementDTO createAnnouncement(AnnouncementDTO dto, Long userId) {
        // 1. Verificación de seguridad: ¿Está el usuario confirmado en ESTE vuelo para poder publicar?
        bookingRepository.findByUserId(userId).stream()
                .filter(b -> b.getFlight().getFlightNumber().equalsIgnoreCase(dto.getFlightNumber()) && b.getIsConfirmed())
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes publicar si no estás verificado en este vuelo"));

        // 2. Buscar el vuelo y el autor
        Flight flight = flightRepository.findByFlightNumber(dto.getFlightNumber())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vuelo no encontrado"));

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        // 3. Mapeo y guardado
        Announcement entity = mapper.toEntity(dto);
        entity.setFlight(flight);
        entity.setAuthor(author);
        entity.setCreatedAt(LocalDateTime.now());

        return mapper.toDTO(repository.save(entity));
    }

    @Override
    @Transactional
    public AnnouncementDTO updateAnnouncement(Long id, AnnouncementDTO dto, Long userId) {
        // 1. Buscar el anuncio existente
        Announcement ann = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anuncio no encontrado"));

        // 2. SEGURIDAD: Verificar que el que edita es el dueño
        if (!ann.getAuthor().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para editar este anuncio");
        }

        // 3. Actualizar campos permitidos
        ann.setTitle(dto.getTitle());
        ann.setDescription(dto.getDescription());
        ann.setSeatsAvailable(dto.getSeatsAvailable());

        // Convertir el String de la categoría al Enum correspondiente
        try {
            ann.setCategory(Announcement.Category.valueOf(dto.getCategory()));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoría no válida");
        }

        // 4. Guardar y devolver el DTO actualizado
        return mapper.toDTO(repository.save(ann));
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Announcement ann = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!ann.getAuthor().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes borrar anuncios de otros");
        }
        repository.delete(ann);
    }
}

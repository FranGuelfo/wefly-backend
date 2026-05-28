package com.wefly.wefly.service.impl;

import com.wefly.wefly.mapper.AnnouncementMapper;
import com.wefly.wefly.model.Announcement;
import com.wefly.wefly.model.dto.AnnouncementDTO;
import com.wefly.wefly.repository.AnnouncementRepository;
import com.wefly.wefly.repository.BookingRepository;
import com.wefly.wefly.service.AnnouncementService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository repository;
    private final BookingRepository bookingRepository; // Para vuestras reglas de negocio/seguridad
    private final AnnouncementMapper mapper;

    @Override
    @Transactional
    public AnnouncementDTO createAnnouncement(AnnouncementDTO dto) {
        log.info("Procesando creación de anuncio en Service para el vuelo: {}", dto.getFlightNumber());

        // 1. Verificación de seguridad opcional (Actívala si usas la tabla bookings para validar el pasaje):
        /*
        bookingRepository.findByUserId(dto.getUserId()).stream()
                .filter(b -> b.getFlight().getFlightNumber().equalsIgnoreCase(dto.getFlightNumber()) && b.getIsConfirmed())
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes publicar si no estás verificado en este vuelo"));
        */

        // 2. Mapeamos el DTO entrante a la Entidad real
        Announcement entity = mapper.toEntity(dto);
        entity.setCreatedAt(LocalDateTime.now()); // Forzamos la fecha de creación del servidor

        // 3. Guardamos a través del repositorio plano
        Announcement saved = repository.save(entity);
        log.info("Anuncio guardado con éxito con ID: {}", saved.getId());

        return mapper.toDTO(saved);
    }

    @Override
    public List<AnnouncementDTO> getAnnouncementsByFlight(String flightNumber) {
        log.info("Buscando en repositorio anuncios para el vuelo: {}", flightNumber);

        // Llama al método exacto de vuestro repositorio: findByFlightNumberOrderByCreatedAtDesc
        return repository.findByFlightNumberOrderByCreatedAtDesc(flightNumber)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public AnnouncementDTO updateAnnouncement(Long id, AnnouncementDTO dto, Long userId) {
        Announcement ann = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anuncio no encontrado"));

        // Verifica con el nuevo campo authorId
        if (!ann.getAuthorId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso");
        }

        // Actualiza los nuevos campos
        ann.setTitle(dto.getTitle());
        ann.setDescription(dto.getDescription());
        ann.setSeatsAvailable(dto.getSeatsAvailable());
        ann.setCategory(dto.getCategory());
        ann.setOrigin(dto.getOrigin());
        ann.setDestination(dto.getDestination());

        return mapper.toDTO(repository.save(ann));
    }

    @Override
    @Transactional
    public void delete(Long id, Long userId) {
        // 1. Buscar el anuncio existente
        Announcement ann = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anuncio no encontrado"));

        // 2. SEGURIDAD: Verificar propiedad antes de borrar
        if (!ann.getAuthorId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes borrar anuncios de otros pasajeros");
        }

        repository.delete(ann);
        log.info("Anuncio ID: {} eliminado físicamente de la base de datos", id);
    }
}
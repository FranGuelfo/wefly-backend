package com.wefly.wefly.repository;

import com.wefly.wefly.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // Recupera los mensajes de un anuncio ordenados por fecha
    List<Message> findByAnnouncementIdOrderByTimestampAsc(Long announcementId);
}
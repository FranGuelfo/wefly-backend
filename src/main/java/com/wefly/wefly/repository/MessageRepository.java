package com.wefly.wefly.repository;

import com.wefly.wefly.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // 1. Añadido el guion bajo (_)
    List<Message> findByAnnouncement_IdOrderByTimestampAsc(Long announcementId);

    // 2. Añadido el guion bajo (_)
    long countByAnnouncement_IdAndTimestampAfter(Long announcementId, LocalDateTime timestamp);

    // 3. Añadido el guion bajo (_)
    long countByAnnouncement_Id(Long announcementId);
}
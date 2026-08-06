package com.wefly.wefly.repository;

import com.wefly.wefly.model.ChatReadProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatReadProgressRepository extends JpaRepository<ChatReadProgress, Long> {
    Optional<ChatReadProgress> findByUserIdAndAnnouncementId(Long userId, Long announcementId);
}

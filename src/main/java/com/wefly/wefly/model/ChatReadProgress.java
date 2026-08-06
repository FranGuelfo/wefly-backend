package com.wefly.wefly.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "chat_read_progress", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "announcement_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatReadProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "announcement_id", nullable = false)
    private Long announcementId;

    @Column(name = "last_read_at", nullable = false)
    private LocalDateTime lastReadAt;

    public ChatReadProgress(Long userId, Long announcementId, LocalDateTime lastReadAt) {
        this.userId = userId;
        this.announcementId = announcementId;
        this.lastReadAt = lastReadAt;
    }
}

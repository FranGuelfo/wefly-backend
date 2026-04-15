package com.wefly.wefly.model.dto;

import com.wefly.wefly.model.Announcement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long id;
    private String name;
    private String email;
    private String bio;
    private String profilePictureUrl;
    private List<Announcement> announcements;
}

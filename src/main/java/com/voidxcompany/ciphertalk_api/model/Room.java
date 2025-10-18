package com.voidxcompany.ciphertalk_api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    private Long id;
    private String name;
    private String description;
    private String address;
    private boolean isPublic;
    private LocalDateTime createdAt;
    private List<Tag> tags;
}

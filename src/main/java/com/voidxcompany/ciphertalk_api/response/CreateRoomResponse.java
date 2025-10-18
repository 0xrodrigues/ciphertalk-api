package com.voidxcompany.ciphertalk_api.response;

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
public class CreateRoomResponse {
    private Long roomId;
    private String address;
    private String name;
    private String description;
    private Long hostUserId;
    private Integer maxUsers;
    private String visibility;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> tags;
}

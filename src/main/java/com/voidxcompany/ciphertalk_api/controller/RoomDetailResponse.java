package com.voidxcompany.ciphertalk_api.controller;

import com.voidxcompany.ciphertalk_api.model.Room;
import com.voidxcompany.ciphertalk_api.model.RoomVisibility;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RoomDetailResponse {
    private Long id;
    private Long hoster;
    private int maxUsers;
    private String address;
    private String name;
    private String description;
    private RoomVisibility visibility;
    private LocalDateTime createdAt;
    private int currentUsers;

    public static RoomDetailResponse fromRoom(Room room, int currentUsers, int maxUsers) {
        return new RoomDetailResponse(
                room.getId(),
                room.getHoster(),
                maxUsers,
                room.getAddress(),
                room.getName(),
                room.getDescription(),
                room.getVisibility(),
                room.getCreatedAt(),
                currentUsers
        );
    }
}

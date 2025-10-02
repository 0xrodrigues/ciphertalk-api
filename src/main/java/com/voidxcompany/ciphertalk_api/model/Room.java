package com.voidxcompany.ciphertalk_api.model;

import com.voidxcompany.ciphertalk_api.controller.request.CreateRoomRequest;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Room {
    private Long id;
    private Long hoster;
    private int maxUsers;
    private String address;
    private String name;
    private String description;
    private RoomVisibility visibility;
    private LocalDateTime createdAt;

    public Room(CreateRoomRequest req) {
        this.address = UUID.randomUUID().toString();
        this.name = req.getName();
        this.description = req.getDescription();
        this.visibility = req.getVisibility();
        this.createdAt = LocalDateTime.now();
        this.hoster = req.getHoster();
        this.maxUsers = req.getMaxUsers();
    }

}

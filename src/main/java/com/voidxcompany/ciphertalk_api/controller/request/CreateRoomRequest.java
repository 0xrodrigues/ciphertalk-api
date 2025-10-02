package com.voidxcompany.ciphertalk_api.controller.request;

import com.voidxcompany.ciphertalk_api.model.RoomVisibility;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CreateRoomRequest {
    private String name;
    private String description;
    private RoomVisibility visibility;
    private Long hoster;
    private int maxUsers;
}

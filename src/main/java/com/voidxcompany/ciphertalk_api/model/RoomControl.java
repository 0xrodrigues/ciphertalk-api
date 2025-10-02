package com.voidxcompany.ciphertalk_api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;
import java.util.List;

@RedisHash("RoomControl")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomControl {
    @Id
    private String roomAddress;
    private String password;
    private Long hoster;
    private List<Long> users = new ArrayList<>();
    private int maxUsers;
    private int currentUsers;

    public void addUser(Long userId) {
        this.users.add(userId);
        this.currentUsers++;
    }

    public void removeUser(Long userId) {
        this.users.remove(userId);
        this.currentUsers--;
    }

    public RoomControl(Room room) {
        this.roomAddress = room.getAddress();
        this.maxUsers = room.getMaxUsers();
        this.hoster = room.getHoster();
        this.currentUsers = 0;
    }
}

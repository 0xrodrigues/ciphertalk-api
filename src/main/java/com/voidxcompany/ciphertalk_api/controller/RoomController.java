package com.voidxcompany.ciphertalk_api.controller;

import com.voidxcompany.ciphertalk_api.controller.request.CreateRoomRequest;
import com.voidxcompany.ciphertalk_api.model.Room;
import com.voidxcompany.ciphertalk_api.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {

    private final RoomRepository repository;

    @PostMapping
    public ResponseEntity<String> add(@RequestBody CreateRoomRequest request) {
        try {
            var domain = new Room(request);
            repository.createNewRoom(domain);
            return ResponseEntity.ok("Room added successfully");
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Failed to add room");
        }
    }

    @GetMapping
    public ResponseEntity<List<Room>> getAll() {
        try {
            var rooms = repository.getAllPublicRooms();
            return ResponseEntity.ok(rooms);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(List.of());
        }
    }
}

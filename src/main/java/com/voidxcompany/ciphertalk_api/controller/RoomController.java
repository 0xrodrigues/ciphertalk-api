package com.voidxcompany.ciphertalk_api.controller;

import com.voidxcompany.ciphertalk_api.controller.request.CreateRoomRequest;
import com.voidxcompany.ciphertalk_api.model.Room;
import com.voidxcompany.ciphertalk_api.repository.RoomControlRepository;
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
    private final RoomControlRepository roomControlRepository;

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

    @GetMapping("/details/{roomAddress}")
    public ResponseEntity<RoomDetailResponse> getRoomDetails(@PathVariable String roomAddress) {
        try {
            var room = repository.getRoomByAddress(roomAddress);
            var roomControl = roomControlRepository.findById(roomAddress)
                    .orElseThrow(() -> new RuntimeException("Room not found"));
            var response = RoomDetailResponse.fromRoom(room, roomControl.getCurrentUsers(), roomControl.getMaxUsers());
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}

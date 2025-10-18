package com.voidxcompany.ciphertalk_api.controller;

import com.voidxcompany.ciphertalk_api.request.CreateRoomRequest;
import com.voidxcompany.ciphertalk_api.response.CreateRoomResponse;
import com.voidxcompany.ciphertalk_api.response.FindRoomResponse;
import com.voidxcompany.ciphertalk_api.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<CreateRoomResponse> createRoom(@Valid @RequestBody CreateRoomRequest request) {
        CreateRoomResponse response = roomService.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/public")
    public ResponseEntity<List<FindRoomResponse>> getPublicRooms() {
        List<FindRoomResponse> rooms = roomService.findPublicRooms();
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/search")
    public ResponseEntity<List<FindRoomResponse>> searchRooms(@RequestParam("q") String query) {
        List<FindRoomResponse> rooms = roomService.searchRooms(query);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{address}")
    public ResponseEntity<FindRoomResponse> getRoomByAddress(@PathVariable String address) {
        FindRoomResponse room = roomService.findRoomByAddress(address);
        return ResponseEntity.ok(room);
    }
}

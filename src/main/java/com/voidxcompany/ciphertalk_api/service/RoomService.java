package com.voidxcompany.ciphertalk_api.service;

import com.voidxcompany.ciphertalk_api.model.Room;
import com.voidxcompany.ciphertalk_api.model.Tag;
import com.voidxcompany.ciphertalk_api.model.User;
import com.voidxcompany.ciphertalk_api.repository.RoomRepository;
import com.voidxcompany.ciphertalk_api.repository.UserRepository;
import com.voidxcompany.ciphertalk_api.request.CreateRoomRequest;
import com.voidxcompany.ciphertalk_api.response.CreateRoomResponse;
import com.voidxcompany.ciphertalk_api.response.FindRoomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public CreateRoomResponse createRoom(CreateRoomRequest request) {
        // Find or create host user
        User hostUser = userRepository.findOrCreate(request.getHostUsername());
        
        // Generate unique UUID address
        String address = UUID.randomUUID().toString();

        // Build tags
        List<Tag> tags = null;
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            tags = request.getTags().stream()
                    .map(tagName -> Tag.builder().name(tagName).build())
                    .collect(Collectors.toList());
        }

        // Determine visibility
        Room.RoomVisibility visibility = Room.RoomVisibility.PUBLIC;
        if (request.getVisibility() != null) {
            try {
                visibility = Room.RoomVisibility.valueOf(request.getVisibility().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Default to PUBLIC if invalid
            }
        }

        // Create room
        Room room = Room.builder()
                .address(address)
                .name(request.getName())
                .description(request.getDescription())
                .hostUserId(hostUser.getUserId())
                .maxUsers(request.getMaxUsers() != null ? request.getMaxUsers() : 10)
                .visibility(visibility)
                .tags(tags)
                .build();

        Room savedRoom = roomRepository.create(room);

        return mapToCreateRoomResponse(savedRoom);
    }

    public List<FindRoomResponse> findPublicRooms() {
        List<Room> rooms = roomRepository.findPublicRooms();
        return rooms.stream()
                .map(this::mapToFindRoomResponse)
                .collect(Collectors.toList());
    }

    public List<FindRoomResponse> searchRooms(String query) {
        List<Room> rooms = roomRepository.searchRooms(query);
        return rooms.stream()
                .map(this::mapToFindRoomResponse)
                .collect(Collectors.toList());
    }

    public FindRoomResponse findRoomByAddress(String address) {
        Room room = roomRepository.findByAddress(address)
                .orElseThrow(() -> new RuntimeException("Room not found with address: " + address));
        return mapToFindRoomResponse(room);
    }

    private CreateRoomResponse mapToCreateRoomResponse(Room room) {
        return CreateRoomResponse.builder()
                .roomId(room.getRoomId())
                .address(room.getAddress())
                .name(room.getName())
                .description(room.getDescription())
                .hostUserId(room.getHostUserId())
                .maxUsers(room.getMaxUsers())
                .visibility(room.getVisibility().name())
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .tags(room.getTags() != null ? 
                        room.getTags().stream().map(Tag::getName).collect(Collectors.toList()) : 
                        List.of())
                .build();
    }

    private FindRoomResponse mapToFindRoomResponse(Room room) {
        return FindRoomResponse.builder()
                .roomId(room.getRoomId())
                .address(room.getAddress())
                .name(room.getName())
                .description(room.getDescription())
                .hostUserId(room.getHostUserId())
                .maxUsers(room.getMaxUsers())
                .visibility(room.getVisibility().name())
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .tags(room.getTags() != null ? 
                        room.getTags().stream().map(Tag::getName).collect(Collectors.toList()) : 
                        List.of())
                .build();
    }
}

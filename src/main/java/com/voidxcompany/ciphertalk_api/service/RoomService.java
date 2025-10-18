package com.voidxcompany.ciphertalk_api.service;

import com.voidxcompany.ciphertalk_api.model.Room;
import com.voidxcompany.ciphertalk_api.model.Tag;
import com.voidxcompany.ciphertalk_api.repository.RoomRepository;
import com.voidxcompany.ciphertalk_api.request.CreateRoomRequest;
import com.voidxcompany.ciphertalk_api.response.CreateRoomResponse;
import com.voidxcompany.ciphertalk_api.response.FindRoomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public CreateRoomResponse createRoom(CreateRoomRequest request) {
        // Generate unique address (slug)
        String address = generateUniqueAddress();

        // Build tags
        List<Tag> tags = null;
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            tags = request.getTags().stream()
                    .map(tagName -> Tag.builder().name(tagName).build())
                    .collect(Collectors.toList());
        }

        // Create room
        Room room = Room.builder()
                .name(request.getName())
                .description(request.getDescription())
                .address(address)
                .isPublic(request.getIsPublic() != null ? request.getIsPublic() : true)
                .createdAt(LocalDateTime.now())
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

    private String generateUniqueAddress() {
        // Generate a short unique slug
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private CreateRoomResponse mapToCreateRoomResponse(Room room) {
        return CreateRoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .description(room.getDescription())
                .address(room.getAddress())
                .isPublic(room.isPublic())
                .createdAt(room.getCreatedAt())
                .tags(room.getTags() != null ? 
                        room.getTags().stream().map(Tag::getName).collect(Collectors.toList()) : 
                        List.of())
                .build();
    }

    private FindRoomResponse mapToFindRoomResponse(Room room) {
        return FindRoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .description(room.getDescription())
                .address(room.getAddress())
                .isPublic(room.isPublic())
                .createdAt(room.getCreatedAt())
                .tags(room.getTags() != null ? 
                        room.getTags().stream().map(Tag::getName).collect(Collectors.toList()) : 
                        List.of())
                .build();
    }
}

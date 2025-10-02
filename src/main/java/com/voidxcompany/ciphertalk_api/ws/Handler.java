package com.voidxcompany.ciphertalk_api.ws;

import com.voidxcompany.ciphertalk_api.model.Room;
import com.voidxcompany.ciphertalk_api.model.RoomControl;
import com.voidxcompany.ciphertalk_api.model.RoomVisibility;
import com.voidxcompany.ciphertalk_api.model.WebSocketConnectionParams;
import com.voidxcompany.ciphertalk_api.repository.RoomControlRepository;
import com.voidxcompany.ciphertalk_api.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class Handler implements WebSocketHandler {

    private final RoomRepository roomRepository;
    private final RoomControlRepository roomControlRepository;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        try {
            WebSocketConnectionParams params = extractConnectionParams(session);
            Room room = roomRepository.getRoomByAddress(params.getRoomAddress());
            if (room != null) {
                var roomControl = getRoomControl(params.getRoomAddress());
                if (!isRoomFull(roomControl)) {
                    if (RoomVisibility.PRIVATE.equals(room.getVisibility())) {
                        if (params.hasPassword() && !params.getPassword().equals(roomControl.getPassword())) {
                            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Invalid password"));
                            return;
                        }
                    }

                    roomControl.addUser(params.getUserId());
                    roomControlRepository.save(roomControl);
                } else {
                    session.close(CloseStatus.POLICY_VIOLATION.withReason("Room is full"));
                    return;
                }
            } else {
                session.close();
            }
        } catch (IllegalArgumentException e) {
            session.close(CloseStatus.BAD_DATA.withReason(e.getMessage()));
        } catch (Exception e) {
            session.close(CloseStatus.SERVER_ERROR.withReason("Internal server error"));
            throw e;
        }

    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private WebSocketConnectionParams extractConnectionParams(WebSocketSession session) {
        String query = session.getUri() != null ? session.getUri().getQuery() : null;
        String roomAddress = null;
        Long userId = null;
        String password = null;

        if (query != null) {
            for (String param : query.split("&")) {
                String[] parts = param.split("=");
                if (parts.length == 2) {
                    String key = parts[0];
                    String value = parts[1];

                    switch (key) {
                        case "address" -> roomAddress = value;
                        case "user" -> userId = parseLongSafely(value);
                        case "password" -> password = value;
                    }
                }
            }
        }

        if (roomAddress == null || userId == null) {
            throw new IllegalArgumentException("Missing required parameters: address and user are required");
        }

        return new WebSocketConnectionParams(roomAddress, userId, password);
    }

    private Long parseLongSafely(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid user format: " + value);
        }
    }

    private boolean isRoomFull(RoomControl roomControl) {
        return roomControl != null && roomControl.getCurrentUsers() >= roomControl.getMaxUsers();
    }
    private RoomControl getRoomControl(String address) {
        return roomControlRepository.findById(address)
                .orElseThrow(() -> new RuntimeException("Room not found"));
    }
}

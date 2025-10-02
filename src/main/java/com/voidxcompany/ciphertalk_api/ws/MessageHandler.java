package com.voidxcompany.ciphertalk_api.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voidxcompany.ciphertalk_api.model.*;
import com.voidxcompany.ciphertalk_api.repository.RoomControlRepository;
import com.voidxcompany.ciphertalk_api.repository.RoomRepository;
import com.voidxcompany.ciphertalk_api.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageHandler implements WebSocketHandler {

    private final SessionManager sessionManager;
    private final MessageService messageService;

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
                    sessionManager.addSession(params.getUserId(), params.getRoomAddress(), session);

                    List<String> messages = messageService.retrieveMessages(params.getRoomAddress());
                    for (String msg : messages) {
                        session.sendMessage(new TextMessage(msg));
                    }
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

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (message instanceof TextMessage text) {
            String messageAsString = text.getPayload();

            try {
                ChatMessage chatMessage = parseTextMessageToObject(messageAsString);
                log.info("Received message: {}", chatMessage);

                String room = chatMessage.getRoomAddress();
                RoomControl roomControl = getRoomControl(room);

                for (Long user : roomControl.getUsers()) {
                    String sessionToken = user + "_" + room;
                    WebSocketSession targetSession = sessionManager.getSession(sessionToken);
                    if (targetSession != null && targetSession.isOpen()) {
                        targetSession.sendMessage(new TextMessage(messageAsString));
                    }
                }

                messageService.storeMessage(room, messageAsString);
            } catch (Exception ex) {
                log.error("Error processing message: {}",ex.getMessage(),ex);
                session.sendMessage(new TextMessage("Error: Invalid message format"));
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("Erro de transporte WebSocket: {}", exception.getMessage());
        sessionManager.removeSession(session);
        try {
            session.close();
        } catch (IOException e) {
            log.error("Erro ao fechar sessão após falha de transporte: {}", e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        WebSocketConnectionParams params = extractConnectionParams(session);
        String roomAddress = params.getRoomAddress();
        RoomControl roomControl = getRoomControl(roomAddress);
        roomControl.removeUser(params.getUserId());
        roomControlRepository.save(roomControl);
        sessionManager.removeSession(session);
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

    private ChatMessage parseTextMessageToObject(String messageAsString) {
        try {
            ChatMessage chatMessage = objectMapper.readValue(messageAsString, ChatMessage.class);
            chatMessage.setMessageId("msg-" + UUID.randomUUID());
            return chatMessage;
        } catch (Exception ex) {
            log.error("Error processing message: {}",ex.getMessage(),ex);
            throw new RuntimeException("Failed to parse message", ex);
        }
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

package com.voidxcompany.ciphertalk_api.controller;

import com.voidxcompany.ciphertalk_api.model.MessagePayload;
import com.voidxcompany.ciphertalk_api.service.ParticipantTracker;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class RealtimeController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ParticipantTracker participantTracker;

    @MessageMapping("/room/{roomAddress}/join")
    public void joinRoom(@DestinationVariable String roomAddress, @Payload MessagePayload payload) {
        int participantCount = participantTracker.addParticipant(roomAddress);
        
        MessagePayload joinMessage = MessagePayload.builder()
                .type("join")
                .roomAddress(roomAddress)
                .username(payload.getUsername())
                .content(payload.getUsername() + " joined the room")
                .timestamp(System.currentTimeMillis())
                .participantCount(participantCount)
                .build();

        messagingTemplate.convertAndSend("/topic/room/" + roomAddress, joinMessage);
    }

    @MessageMapping("/room/{roomAddress}/leave")
    public void leaveRoom(@DestinationVariable String roomAddress, @Payload MessagePayload payload) {
        int participantCount = participantTracker.removeParticipant(roomAddress);
        
        MessagePayload leaveMessage = MessagePayload.builder()
                .type("leave")
                .roomAddress(roomAddress)
                .username(payload.getUsername())
                .content(payload.getUsername() + " left the room")
                .timestamp(System.currentTimeMillis())
                .participantCount(participantCount)
                .build();

        messagingTemplate.convertAndSend("/topic/room/" + roomAddress, leaveMessage);
    }

    @MessageMapping("/room/{roomAddress}/message")
    public void sendMessage(@DestinationVariable String roomAddress, @Payload MessagePayload payload) {
        int participantCount = participantTracker.getParticipantCount(roomAddress);
        
        MessagePayload message = MessagePayload.builder()
                .type("message")
                .roomAddress(roomAddress)
                .username(payload.getUsername())
                .content(payload.getContent())
                .timestamp(System.currentTimeMillis())
                .participantCount(participantCount)
                .build();

        messagingTemplate.convertAndSend("/topic/room/" + roomAddress, message);
    }
}

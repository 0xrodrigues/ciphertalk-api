package com.voidxcompany.ciphertalk_api.service;

import com.voidxcompany.ciphertalk_api.model.ChatMessage;
import com.voidxcompany.ciphertalk_api.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;

    public void storeMessage(String room, ChatMessage chatMessage) {
        try {
            messageRepository.save(chatMessage);
            log.debug("Message stored for room: {}", room);
        } catch (Exception e) {
            log.error("Error storing message: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to store message", e);
        }
    }

    public List<ChatMessage> retrieveMessages(String room) {
        List<ChatMessage> messages = messageRepository.findByRoomAddressOrderByTimestampAsc(room);
        log.debug("Retrieved {} messages for room: {}", messages.size(), room);
        return messages;
    }
    
    public void clearRoomMessages(String room) {
        messageRepository.deleteByRoomAddress(room);
        log.info("Cleared all messages for room: {}", room);
    }
}

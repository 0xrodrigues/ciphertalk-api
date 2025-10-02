package com.voidxcompany.ciphertalk_api.service;

import com.voidxcompany.ciphertalk_api.model.ChatMessage;
import com.voidxcompany.ciphertalk_api.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final Duration TTL = Duration.ofHours(1);
    private final MessageRepository messageRepository;

    public void storeMessage(String roomAddress, String message) {
        String key = "queue:" + roomAddress;

        log.info("Chave Redis: {}", key);
        log.info("Mensagem: {}", message);

        redisTemplate.opsForList().rightPush(key, message);
        redisTemplate.expire(key, TTL);
    }

    public List<String> retrieveMessages(String roomAddress) {
        String key = "queue:" + roomAddress;
        List<String> messages = redisTemplate.opsForList().range(key, 0, -1);
        redisTemplate.delete(key);
        return messages != null ? messages : Collections.emptyList();
    }
    
    public void clearRoomMessages(String room) {
        messageRepository.deleteByRoomAddress(room);
        log.info("Cleared all messages for room: {}", room);
    }
}

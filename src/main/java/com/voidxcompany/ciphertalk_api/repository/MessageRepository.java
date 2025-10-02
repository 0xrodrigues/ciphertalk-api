package com.voidxcompany.ciphertalk_api.repository;

import com.voidxcompany.ciphertalk_api.model.ChatMessage;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MessageRepository extends CrudRepository<ChatMessage, String> {
    List<ChatMessage> findByRoomAddressOrderByTimestampAsc(String roomAddress);
    void deleteByRoomAddress(String roomAddress);
}

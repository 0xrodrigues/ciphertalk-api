package com.voidxcompany.ciphertalk_api.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ParticipantTracker {

    private final Map<String, AtomicInteger> roomParticipants = new ConcurrentHashMap<>();

    public int addParticipant(String roomAddress) {
        roomParticipants.putIfAbsent(roomAddress, new AtomicInteger(0));
        return roomParticipants.get(roomAddress).incrementAndGet();
    }

    public int removeParticipant(String roomAddress) {
        AtomicInteger count = roomParticipants.get(roomAddress);
        if (count != null) {
            int newCount = count.decrementAndGet();
            if (newCount <= 0) {
                roomParticipants.remove(roomAddress);
                return 0;
            }
            return newCount;
        }
        return 0;
    }

    public int getParticipantCount(String roomAddress) {
        AtomicInteger count = roomParticipants.get(roomAddress);
        return count != null ? count.get() : 0;
    }
}

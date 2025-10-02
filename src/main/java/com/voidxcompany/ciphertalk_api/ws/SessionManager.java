package com.voidxcompany.ciphertalk_api.ws;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void addSession(Long userId, String roomAddress, WebSocketSession session) {
        String key = userId + "_" + roomAddress;
        sessions.put(key, session);
    }

    public void removeSession(WebSocketSession session) {
        sessions.values().remove(session);
    }

    public WebSocketSession getSession(String key) {
        return sessions.get(key);
    }

    public Map<String, WebSocketSession> getAllSessions() {
        return new ConcurrentHashMap<>(sessions);
    }
}
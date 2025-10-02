package com.voidxcompany.ciphertalk_api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WebSocketConnectionParams {
    private final String roomAddress;
    private final Long userId;
    private final String password;

    public boolean hasPassword() {
        return password != null && !password.isEmpty();
    }
}

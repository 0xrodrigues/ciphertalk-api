package com.voidxcompany.ciphertalk_api.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserEventNotification {
    private Long user;
    private String event;
}

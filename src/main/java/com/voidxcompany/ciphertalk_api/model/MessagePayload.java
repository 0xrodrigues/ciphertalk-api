package com.voidxcompany.ciphertalk_api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessagePayload {
    private String type;
    private String roomAddress;
    private String username;
    private String content;
    private Long timestamp;
    private Integer participantCount;
}

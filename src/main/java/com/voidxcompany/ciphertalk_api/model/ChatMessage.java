package com.voidxcompany.ciphertalk_api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChatMessage {

    @JsonProperty("message")
    private String message;
    @JsonProperty("message_id")
    private String messageId;
    @JsonProperty("sender")
    private Long sender;
    @JsonProperty("timestamp")
    private Timestamp timestamp;
    @JsonProperty("room_address")
    private String roomAddress;
    @JsonProperty("type")
    private ChatMessageType type;

}

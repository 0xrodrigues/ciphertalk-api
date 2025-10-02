package com.voidxcompany.ciphertalk_api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@RedisHash("ChatMessage")
public class ChatMessage implements Serializable {

    @Id
    private String id;

    @JsonProperty("message_id")
    private String messageId;

    @JsonProperty("message")
    private String message;

    @JsonProperty("sender")
    private Long sender;

    @JsonProperty("timestamp")
    private Timestamp timestamp;

    @JsonProperty("room_address")
    private String roomAddress;

    @JsonProperty("type")
    private ChatMessageType type;
}

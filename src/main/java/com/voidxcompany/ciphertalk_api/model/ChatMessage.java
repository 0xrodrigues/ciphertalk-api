package com.voidxcompany.ciphertalk_api.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@RedisHash("ChatMessage")
public class ChatMessage implements Serializable {

    @Id
    private String id;
    private Long sender;
    private String message;
    private Timestamp moment;
}

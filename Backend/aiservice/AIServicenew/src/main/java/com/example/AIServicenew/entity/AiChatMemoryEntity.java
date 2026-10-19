package com.example.AIServicenew.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.LocalDateTime;

@Document(collection = "aitable")
@Data
public class AiChatMemoryEntity {

    @Id
    private String id;

    @Field("session_key")
    private String sessionKey;

    @Field("message_role")
    private String messageRole;

    @Field("message_text")
    private String messageText;

    @Field("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}

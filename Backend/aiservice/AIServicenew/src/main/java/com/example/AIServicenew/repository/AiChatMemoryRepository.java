package com.example.AIServicenew.repository;

import com.example.AIServicenew.entity.AiChatMemoryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AiChatMemoryRepository extends MongoRepository<AiChatMemoryEntity, String> {
    List<AiChatMemoryEntity> findBySessionKeyOrderByCreatedAtAsc(String sessionKey);
    void deleteBySessionKey(String sessionKey);
}

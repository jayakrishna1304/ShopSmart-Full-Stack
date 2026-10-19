package com.example.AIServicenew.controller;

import com.example.AIServicenew.dto.AiPlatformOutputDto;
import com.example.AIServicenew.dto.ChatPayloadDto;
import com.example.AIServicenew.service.AiChatService;
import com.example.AIServicenew.service.AiEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/shopsmart/ai")
public class AiController {

    @Autowired private AiEngineService aiEngineService;
    @Autowired private AiChatService aiChatService;

    @GetMapping("/insights")
    public ResponseEntity<List<AiPlatformOutputDto>> getSystemAnalyticsInsights() {
        return ResponseEntity.ok(aiEngineService.calculatePlatformInsights());
    }

    @PostMapping("/chatbot")
    public ResponseEntity<String> executeConversationalChatRequest(@RequestBody ChatPayloadDto payload) {
        return ResponseEntity.ok(aiChatService.processChatSession(payload));
    }

    @DeleteMapping("/chatbot/reset")
    public ResponseEntity<String> clearSessionMemory(@RequestParam("role") String role, @RequestParam("id") Integer id) {
        aiChatService.wipeSessionMemory(role, id);
        return ResponseEntity.ok("Conversational memory cleared successfully.");
    }
}

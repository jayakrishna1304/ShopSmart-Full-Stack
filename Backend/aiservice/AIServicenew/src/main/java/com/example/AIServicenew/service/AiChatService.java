package com.example.AIServicenew.service;

import com.example.AIServicenew.dto.AiPlatformOutputDto;
import com.example.AIServicenew.dto.ChatPayloadDto;
import com.example.AIServicenew.dto.ExternalDataDto;
import com.example.AIServicenew.entity.AiChatMemoryEntity;
import com.example.AIServicenew.repository.AiChatMemoryRepository;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AiChatService {

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private AiChatMemoryRepository memoryRepository;

    @Autowired
    private AiEngineService aiEngineService;


    public String processChatSession(ChatPayloadDto payload) {

        // =========================================================
        // 1. VALIDATION
        // =========================================================

        if (chatModel == null) {
            throw new IllegalStateException(
                    "AI ChatModel is not initialized. Check Groq API configuration."
            );
        }

        if (payload == null) {
            return "Invalid request: chat payload is missing.";
        }

        if (payload.getUserRole() == null) {
            return "Invalid request: user role is missing.";
        }

        if (payload.getActiveContextId() == null) {
            return "Invalid request: customer/retailer ID is missing.";
        }

        if (payload.getUserPrompt() == null ||
                payload.getUserPrompt().trim().isEmpty()) {

            return "Please enter a message.";
        }


        // =========================================================
        // 2. SESSION
        // =========================================================

        String sessionKey =
                payload.getUserRole().toLowerCase()
                        + "_"
                        + payload.getActiveContextId();


        // =========================================================
        // 3. GET RAW LIVE DATA
        // =========================================================

        List<ExternalDataDto> cartData;

        List<ExternalDataDto> orderData;

        List<AiPlatformOutputDto> calculatedMetrics;


        try {

            cartData =
                    aiEngineService.getCartData();

        } catch (Exception e) {

            System.err.println(
                    "AI CHAT - CART DATA ERROR: "
                            + e.getMessage()
            );

            cartData = new ArrayList<>();
        }


        try {

            orderData =
                    aiEngineService.getOrderData();

        } catch (Exception e) {

            System.err.println(
                    "AI CHAT - ORDER DATA ERROR: "
                            + e.getMessage()
            );

            orderData = new ArrayList<>();
        }


        try {

            calculatedMetrics =
                    aiEngineService.calculatePlatformInsights();

        } catch (Exception e) {

            System.err.println(
                    "AI CHAT - INSIGHTS ERROR: "
                            + e.getMessage()
            );

            calculatedMetrics = new ArrayList<>();
        }


        // =========================================================
        // 4. DEBUG
        // =========================================================

        System.out.println(
                "AI CHAT - RAW CART RECORDS: "
                        + cartData.size()
        );

        System.out.println(
                "AI CHAT - RAW ORDER RECORDS: "
                        + orderData.size()
        );

        System.out.println(
                "AI CHAT - CALCULATED INSIGHTS: "
                        + calculatedMetrics.size()
        );


        // =========================================================
        // 5. BUILD DATA CONTEXT
        // =========================================================

        String liveDataContext = String.format(

                """
                ==============================
                LIVE CART DATA
                ==============================

                %s


                ==============================
                LIVE ORDER DATA
                ==============================

                %s


                ==============================
                CALCULATED PLATFORM INSIGHTS
                ==============================

                %s
                """,

                cartData,
                orderData,
                calculatedMetrics
        );


        // =========================================================
        // 6. SYSTEM PROMPT
        // =========================================================

        String systemInstructionPrompt;

        if ("retailer".equalsIgnoreCase(
                payload.getUserRole())) {

            systemInstructionPrompt = String.format(

                    """
                    You are the ShopSmart AI Business Advisor.

                    You are assisting Retailer ID %d.

                    You have access to live data retrieved from the
                    ShopSmart Cart Service and Order Service.

                    IMPORTANT RULES:

                    1. Use the supplied live data when answering.
                    2. Never invent data.
                    3. Never claim database access.
                    4. If a requested value exists in LIVE ORDER DATA,
                       use it.
                    5. If a requested value exists in LIVE CART DATA,
                       use it.
                    6. If the requested information does not exist in
                       the supplied data, clearly say it is unavailable.
                    7. Do not tell the user to manually check the app
                       when the answer exists in the supplied data.
                    8. Distinguish raw records from calculated insights.

                    LIVE SHOPSMART DATA:

                    %s

                    You can help with:

                    - sales analysis
                    - order history
                    - customer purchases
                    - customer loyalty
                    - abandoned carts
                    - dormant customers
                    - trending product bundles
                    - business recommendations
                    """,

                    payload.getActiveContextId(),
                    liveDataContext
            );

        } else {

            systemInstructionPrompt = String.format(

                    """
                    You are the ShopSmart Personal Shopping Assistant.

                    You are assisting Customer ID %d.

                    You have access to live data retrieved from the
                    ShopSmart Cart Service and Order Service.

                    IMPORTANT RULES:

                    1. Use LIVE ORDER DATA when answering questions
                       about orders or order history.
                    2. Use LIVE CART DATA when answering questions
                       about cart items.
                    3. Use CALCULATED PLATFORM INSIGHTS for loyalty,
                       dormancy, abandoned cart and recommendation
                       information.
                    4. Never invent orders or cart items.
                    5. Never invent prices, dates, products or totals.
                    6. Never claim direct database access.
                    7. If the requested information exists in the
                       supplied live data, answer using that data.
                    8. Do NOT say "I don't have order history" when
                       LIVE ORDER DATA contains records.
                    9. Do NOT tell the customer to manually check the
                       Orders section if the supplied data contains
                       the requested information.
                    10. If the requested information is genuinely not
                        present, clearly explain that it is unavailable.
                    11. Answer naturally and concisely.

                    LIVE SHOPSMART DATA:

                    %s

                    You can help with:

                    - order history
                    - previous purchases
                    - order totals
                    - order dates
                    - cart items
                    - loyalty tier
                    - abandoned carts
                    - shopping recommendations
                    - promotional incentives
                    """,

                    payload.getActiveContextId(),
                    liveDataContext
            );
        }


        // =========================================================
        // 7. LOAD MEMORY
        // =========================================================

        List<AiChatMemoryEntity> storedRows =
                memoryRepository
                        .findBySessionKeyOrderByCreatedAtAsc(
                                sessionKey
                        );

        List<Message> promptsContextList =
                new ArrayList<>();


        promptsContextList.add(
                new SystemMessage(
                        systemInstructionPrompt
                )
        );


        // =========================================================
        // 8. PREVIOUS CONVERSATION
        // =========================================================

        if (storedRows != null) {

            for (AiChatMemoryEntity row : storedRows) {

                if (row == null ||
                        row.getMessageText() == null ||
                        row.getMessageText().trim().isEmpty()) {

                    continue;
                }

                if ("USER".equalsIgnoreCase(
                        row.getMessageRole())) {

                    promptsContextList.add(
                            new UserMessage(
                                    row.getMessageText()
                            )
                    );

                } else if ("ASSISTANT".equalsIgnoreCase(
                        row.getMessageRole())) {

                    promptsContextList.add(
                            new AssistantMessage(
                                    row.getMessageText()
                            )
                    );
                }
            }
        }


        // =========================================================
        // 9. CURRENT USER MESSAGE
        // =========================================================

        promptsContextList.add(
                new UserMessage(
                        payload.getUserPrompt()
                )
        );


        persistMessageHistoryLog(
                sessionKey,
                "USER",
                payload.getUserPrompt()
        );


        // =========================================================
        // 10. CALL AI
        // =========================================================

        try {

            ChatResponse response =
                    chatModel.call(
                            new Prompt(
                                    promptsContextList
                            )
                    );


            // =====================================================
            // 11. RESPONSE VALIDATION
            // =====================================================

            if (response == null ||
                    response.getResult() == null ||
                    response.getResult().getOutput() == null) {

                System.err.println(
                        "GROQ ERROR: Empty response received."
                );

                return "The AI service returned an empty response. Please try again.";
            }


            String aiReplyText =
                    response
                            .getResult()
                            .getOutput()
                            .getText();


            if (aiReplyText == null ||
                    aiReplyText.trim().isEmpty()) {

                return "The AI service returned an empty response. Please try again.";
            }


            // =====================================================
            // 12. SAVE RESPONSE
            // =====================================================

            persistMessageHistoryLog(
                    sessionKey,
                    "ASSISTANT",
                    aiReplyText
            );


            return aiReplyText;


        } catch (Exception e) {

            System.err.println(
                    "GROQ CONNECTION ERROR: "
                            + e.getClass().getName()
                            + " - "
                            + e.getMessage()
            );

            e.printStackTrace();

            return "The AI service is temporarily unavailable. "
                    + "Please try again in a moment.";
        }
    }


    // =============================================================
    // DELETE CHAT MEMORY
    // =============================================================

    @Transactional
    public void wipeSessionMemory(
            String role,
            Integer id) {

        if (role == null || id == null) {
            return;
        }

        String sessionKey =
                role.toLowerCase() + "_" + id;

        memoryRepository.deleteBySessionKey(
                sessionKey
        );
    }


    // =============================================================
    // SAVE CHAT MESSAGE
    // =============================================================

    private void persistMessageHistoryLog(
            String sessionKey,
            String role,
            String text) {

        if (sessionKey == null ||
                role == null ||
                text == null ||
                text.trim().isEmpty()) {

            return;
        }

        AiChatMemoryEntity memoryRow =
                new AiChatMemoryEntity();

        memoryRow.setSessionKey(sessionKey);
        memoryRow.setMessageRole(role);
        memoryRow.setMessageText(text);

        memoryRepository.save(memoryRow);
    }
}
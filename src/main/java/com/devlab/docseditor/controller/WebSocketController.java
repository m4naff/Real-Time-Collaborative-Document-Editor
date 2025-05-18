package com.devlab.docseditor.controller;

import com.devlab.docseditor.model.dto.websocket.DocumentUpdateMessage;
import com.devlab.docseditor.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final WebSocketService webSocketService;

    /**
     * Handles document load requests
     * @param message the load message
     */
    @MessageMapping("/document.load")
    public void handleDocumentLoad(@Payload DocumentUpdateMessage message) {
        webSocketService.handleDocumentLoad(message);
    }

    /**
     * Handles document update messages from clients
     * @param message the update message
     */
    @MessageMapping("/document.update")
    public void handleDocumentUpdate(@Payload DocumentUpdateMessage message) {
        webSocketService.handleDocumentUpdate(message);
    }

    /**
     * Handles user join notifications
     * @param message the join message
     */
    @MessageMapping("/document.join")
    public void handleUserJoin(@Payload DocumentUpdateMessage message) {
        webSocketService.handleUserJoin(message);
    }

    /**
     * Handles user leave notifications
     * @param message the leave message
     */
    @MessageMapping("/document.leave")
    public void handleUserLeave(@Payload DocumentUpdateMessage message) {
        webSocketService.handleUserLeave(message);
    }
}

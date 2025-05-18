package com.devlab.docseditor.service;

import com.devlab.docseditor.model.dto.websocket.DocumentUpdateMessage;
import com.devlab.docseditor.model.entity.CollaborativeDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;
    private final DocumentService documentService;
    private final CacheService cacheService;
    private final UserService userService;

    public void handleDocumentLoad(DocumentUpdateMessage message) {
        String documentId = message.getDocumentId();
        String username = message.getUserId();
        String userId = userService.findByUsername(username).getId();

        log.debug("User {} requested to load document {}", userId, documentId);

        try {
            String content = cacheService.getCachedDocumentContent(documentId);

            if (content == null) {
                CollaborativeDocument document = documentService.getDocumentById(documentId);
                content = document.getContent();
                cacheService.cacheDocumentContent(documentId, content);
            }

            DocumentUpdateMessage contentMessage = new DocumentUpdateMessage(
                    documentId,
                    content,
                    "system",
                    System.currentTimeMillis());

            messagingTemplate.convertAndSendToUser(
                    userId,
                    "/queue/document." + documentId + ".content",
                    contentMessage);

            log.debug("Sent document content to user {} for document {}", userId, documentId);

        } catch (Exception e) {
            log.error("Error processing document load request", e);
        }
    }

    public void handleDocumentUpdate(DocumentUpdateMessage message) {
        String documentId = message.getDocumentId();
        String username = message.getUserId();
        String userId = userService.findByUsername(username).getId();
        String newContent = message.getContent();

        log.debug("Received update for document {} from user {}", documentId, userId);

        try {
            CollaborativeDocument document = documentService.getDocumentById(documentId);

            if (!userId.equals(document.getOwnerId()) && !"editor".equals(document.getAccessRoles().get(userId))) {
                log.warn("User {} attempted to edit document {} without permission", userId, documentId);
                return;
            }

            cacheService.addActiveUser(documentId, userId);

            cacheService.cacheDocumentContent(documentId, newContent);

            message.setContent(newContent);
            messagingTemplate.convertAndSend("/topic/document." + documentId, message);

            if (Math.random() < 0.8) { // 80% chance to save on each update
                documentService.updateDocument(documentId, newContent, userId);
                log.debug("Saved document {} to database", documentId);
            }

        } catch (Exception e) {
            log.error("Error processing document update", e);
        }
    }

    public void handleUserJoin(DocumentUpdateMessage message) {
        String documentId = message.getDocumentId();
        String username = message.getUserId();
        String userId = userService.findByUsername(username).getId();

        log.debug("User {} joined document {}", userId, documentId);

        try {
            cacheService.addActiveUser(documentId, userId);

            messagingTemplate.convertAndSend(
                    "/topic/document." + documentId + ".join",
                    userId);

            messagingTemplate.convertAndSendToUser(
                    userId,
                    "/queue/document." + documentId + ".users",
                    cacheService.getActiveUsers(documentId));

            String cachedContent = cacheService.getCachedDocumentContent(documentId);
            if (cachedContent != null) {
                DocumentUpdateMessage contentMessage = new DocumentUpdateMessage(
                        documentId,
                        cachedContent,
                        "system",
                        System.currentTimeMillis());
                messagingTemplate.convertAndSendToUser(
                        userId,
                        "/queue/document." + documentId + ".content",
                        contentMessage);
                log.debug("Sent cached content to user {} for document {}", userId, documentId);
            }

        } catch (Exception e) {
            log.error("Error processing user join", e);
        }
    }

    public void handleUserLeave(DocumentUpdateMessage message) {
        String documentId = message.getDocumentId();
        String userId = message.getUserId();

        log.debug("User {} left document {}", userId, documentId);

        try {
            // Remove from active users
            cacheService.removeActiveUser(documentId, userId);

            // Notify other users
            messagingTemplate.convertAndSend(
                    "/topic/document." + documentId + ".leave",
                    userId);

        } catch (Exception e) {
            log.error("Error processing user leave", e);
        }
    }


}

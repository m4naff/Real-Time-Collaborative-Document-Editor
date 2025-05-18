package com.devlab.docseditor.controller;

import com.devlab.docseditor.model.dto.request.CreateDocumentRequest;
import com.devlab.docseditor.model.dto.request.ShareDocumentRequest;
import com.devlab.docseditor.model.dto.request.UpdateDocumentRequest;
import com.devlab.docseditor.model.entity.CollaborativeDocument;
import com.devlab.docseditor.model.entity.DocumentVersion;
import com.devlab.docseditor.service.DocumentService;
import com.devlab.docseditor.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<CollaborativeDocument> createDocument(
            @RequestBody CreateDocumentRequest request,
            Authentication authentication) {
        String userName = authentication.getName();
        var userId = userService.findByUsername(userName).getId();
        CollaborativeDocument document = documentService.createDocument(
                request.getTitle(),
                request.getContent(),
                userId
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(document);
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<CollaborativeDocument> getDocument(
            @PathVariable String documentId,
            Authentication authentication) {
        String userName = authentication.getName();
        var userId = userService.findByUsername(userName).getId();
        CollaborativeDocument document = documentService.getDocumentById(documentId);

        if (!document.getOwnerId().equals(userId) && !document.getAccessRoles().containsKey(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(document);
    }

    @PutMapping("/{documentId}")
    public ResponseEntity<CollaborativeDocument> updateDocument(
            @PathVariable String documentId,
            @RequestBody UpdateDocumentRequest request,
            Authentication authentication) {
        String userName = authentication.getName();
        var userId = userService.findByUsername(userName).getId();
        CollaborativeDocument document = documentService.updateDocument(
                documentId,
                request.getContent(),
                userId
        );
        return ResponseEntity.ok(document);
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable String documentId,
            Authentication authentication) {
        String userName = authentication.getName();
        var userId = userService.findByUsername(userName).getId();
        documentService.deleteDocument(documentId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{documentId}/share")
    public ResponseEntity<CollaborativeDocument> shareDocument(
            @PathVariable String documentId,
            @RequestBody ShareDocumentRequest request,
            Authentication authentication) {
        String userName = authentication.getName();
        var userId = userService.findByUsername(userName).getId();
        CollaborativeDocument document = documentService.shareDocument(
                documentId,
                request.getTargetUserId(),
                request.getRole(),
                userId
        );
        return ResponseEntity.ok(document);
    }

    @GetMapping("/owned")
    public ResponseEntity<List<CollaborativeDocument>> getOwnedDocuments(
            Authentication authentication) {
        String username = authentication.getName();
        var userId = userService.findByUsername(username).getId();
        List<CollaborativeDocument> documents = documentService.getDocumentsByOwner(userId);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/shared")
    public ResponseEntity<List<CollaborativeDocument>> getSharedDocuments(
            Authentication authentication) {
        String username = authentication.getName();
        var userId = userService.findByUsername(username).getId();
        List<CollaborativeDocument> documents = documentService.getDocumentsSharedWithUser(userId);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{documentId}/versions")
    public ResponseEntity<List<DocumentVersion>> getDocumentVersions(
            @PathVariable String documentId,
            Authentication authentication) {
        String username = authentication.getName();
        CollaborativeDocument document = documentService.getDocumentById(documentId);

        var userId = userService.findByUsername(username).getId();

        if (!document.getOwnerId().equals(userId) && !document.getAccessRoles().containsKey(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<DocumentVersion> versions = documentService.getDocumentVersionHistory(documentId);
        return ResponseEntity.ok(versions);
    }
}
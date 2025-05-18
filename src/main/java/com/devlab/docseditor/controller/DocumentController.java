package com.devlab.docseditor.controller;

import com.devlab.docseditor.model.dto.request.CreateDocumentRequest;
import com.devlab.docseditor.model.dto.request.ShareDocumentRequest;
import com.devlab.docseditor.model.dto.request.UpdateDocumentRequest;
import com.devlab.docseditor.model.entity.CollaborativeDocument;
import com.devlab.docseditor.model.entity.DocumentVersion;
import com.devlab.docseditor.service.DocumentService;
import com.devlab.docseditor.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Tag(name = "Documents", description = "API for managing collaborative documents")
@SecurityRequirement(name = "bearerAuth")
public class DocumentController {

    private final DocumentService documentService;
    private final UserService userService;

    @Operation(summary = "Create a new document", description = "Creates a new collaborative document with the current user as owner")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Document created successfully",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = CollaborativeDocument.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
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

    @Operation(summary = "Get document by ID", description = "Retrieves a document by its ID if the user has access")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Document retrieved successfully",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = CollaborativeDocument.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User does not have access to this document"),
        @ApiResponse(responseCode = "404", description = "Document not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{documentId}")
    public ResponseEntity<CollaborativeDocument> getDocument(
            @Parameter(description = "ID of the document to retrieve", required = true)
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

    @Operation(summary = "Update document content", description = "Updates the content of a document if the user has edit access")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Document updated successfully",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = CollaborativeDocument.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User does not have edit access"),
        @ApiResponse(responseCode = "404", description = "Document not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{documentId}")
    public ResponseEntity<CollaborativeDocument> updateDocument(
            @Parameter(description = "ID of the document to update", required = true)
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

    @Operation(summary = "Delete a document", description = "Deletes a document if the user is the owner")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Document deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Only the owner can delete a document"),
        @ApiResponse(responseCode = "404", description = "Document not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> deleteDocument(
            @Parameter(description = "ID of the document to delete", required = true)
            @PathVariable String documentId,
            Authentication authentication) {
        String userName = authentication.getName();
        var userId = userService.findByUsername(userName).getId();
        documentService.deleteDocument(documentId, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Share a document", description = "Shares a document with another user with specified access role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Document shared successfully",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = CollaborativeDocument.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Only the owner can share a document"),
        @ApiResponse(responseCode = "404", description = "Document or target user not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{documentId}/share")
    public ResponseEntity<CollaborativeDocument> shareDocument(
            @Parameter(description = "ID of the document to share", required = true)
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

    @Operation(summary = "Get owned documents", description = "Retrieves all documents owned by the current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Documents retrieved successfully",
                content = @Content(mediaType = "application/json", 
                array = @ArraySchema(schema = @Schema(implementation = CollaborativeDocument.class)))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/owned")
    public ResponseEntity<List<CollaborativeDocument>> getOwnedDocuments(
            Authentication authentication) {
        String username = authentication.getName();
        var userId = userService.findByUsername(username).getId();
        List<CollaborativeDocument> documents = documentService.getDocumentsByOwner(userId);
        return ResponseEntity.ok(documents);
    }

    @Operation(summary = "Get shared documents", description = "Retrieves all documents shared with the current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Documents retrieved successfully",
                content = @Content(mediaType = "application/json", 
                array = @ArraySchema(schema = @Schema(implementation = CollaborativeDocument.class)))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/shared")
    public ResponseEntity<List<CollaborativeDocument>> getSharedDocuments(
            Authentication authentication) {
        String username = authentication.getName();
        var userId = userService.findByUsername(username).getId();
        List<CollaborativeDocument> documents = documentService.getDocumentsSharedWithUser(userId);
        return ResponseEntity.ok(documents);
    }

    @Operation(summary = "Get document version history", description = "Retrieves the version history of a document")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Version history retrieved successfully",
                content = @Content(mediaType = "application/json", 
                array = @ArraySchema(schema = @Schema(implementation = DocumentVersion.class)))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User does not have access to this document"),
        @ApiResponse(responseCode = "404", description = "Document not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{documentId}/versions")
    public ResponseEntity<List<DocumentVersion>> getDocumentVersions(
            @Parameter(description = "ID of the document to get version history for", required = true)
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

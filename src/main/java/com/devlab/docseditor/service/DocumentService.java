package com.devlab.docseditor.service;

import com.devlab.docseditor.exception.OwnerAccessException;
import com.devlab.docseditor.exception.DocumentNotFoundException;
import com.devlab.docseditor.exception.NoEditAccessException;
import com.devlab.docseditor.model.entity.CollaborativeDocument;
import com.devlab.docseditor.model.entity.DocumentVersion;
import com.devlab.docseditor.model.entity.User;
import com.devlab.docseditor.repository.DocumentRepository;
import com.devlab.docseditor.utils.VersioningUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserService userService;
    private final CacheService cacheService;

    /**
     * Create a new document
     * @param title document title
     * @param content initial content
     * @param ownerId ID of the user creating the document
     * @return the created document
     */
    public CollaborativeDocument createDocument(String title, String content, String ownerId) {
        CollaborativeDocument document = CollaborativeDocument.builder()
                .title(title)
                .content(content)
                .ownerId(ownerId)
                .accessRoles(new HashMap<>())
                .versions(new ArrayList<>())
                .build();

        var initialVersion = VersioningUtil.createNewVersion(document, content, ownerId);
        document.getVersions().add(initialVersion);
        
        return documentRepository.save(document);
    }

    /**
     * Get a document by ID
     * @param documentId the document ID
     * @return the document
     * @throws NoSuchElementException if document not found
     */
    public CollaborativeDocument getDocumentById(String documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found with ID: " + documentId));
    }

    /**
     * Update document content
     * @param documentId the document ID
     * @param newContent the new content
     * @param userId the ID of the user making the update
     * @return the updated document
     * @throws NoSuchElementException if document not found
     * @throws NoEditAccessException if user doesn't have edit access
     */
    public CollaborativeDocument updateDocument(String documentId, String newContent, String userId) {
        CollaborativeDocument document = getDocumentById(documentId);

        if (!hasEditAccess(document, userId)) {
            throw new NoEditAccessException("User does not have edit access to this document: " + userId);
        }

        DocumentVersion newVersion = VersioningUtil.createNewVersion(document, newContent, userId);

        document.setContent(newContent);
        document.getVersions().add(newVersion);
        
        return documentRepository.save(document);
    }

    /**
     * Delete a document
     * @param documentId the document ID
     * @param userId the ID of the user requesting deletion
     * @throws NoSuchElementException if document not found
     * @throws IllegalArgumentException if user is not the owner
     */
    public void deleteDocument(String documentId, String userId) {
        CollaborativeDocument document = getDocumentById(documentId);

        if (!document.getOwnerId().equals(userId)) {
            throw new OwnerAccessException("Only the document owner can delete it");
        }
        
        documentRepository.delete(document);
    }

    /**
     * Share a document with another user
     * @param documentId the document ID
     * @param targetUserId the ID of the user to share with
     * @param role the access role ("viewer" or "editor")
     * @param currentUserId the ID of the user sharing the document
     * @return the updated document
     * @throws NoSuchElementException if document or user not found
     * @throws IllegalArgumentException if current user is not the owner
     */
    public CollaborativeDocument shareDocument(String documentId, String targetUserId, String role, String currentUserId) {
        if (!role.equals("viewer") && !role.equals("editor")) {
            throw new IllegalArgumentException("Role must be either 'viewer' or 'editor'");
        }
        
        CollaborativeDocument document = getDocumentById(documentId);

        if (!document.getOwnerId().equals(currentUserId)) {
            throw new OwnerAccessException("Only the document owner can share it");
        }

        User targetUser = userService.findById(targetUserId);
        document.getAccessRoles().put(targetUserId, role);

        targetUser.getSharedDocumentIds().add(documentId);
        userService.save(targetUser);
        
        return documentRepository.save(document);
    }

    /**
     * Get all documents owned by a user
     * @param userId the user ID
     * @return list of documents
     */
    public List<CollaborativeDocument> getDocumentsByOwner(String userId) {
        return documentRepository.findByOwnerId(userId);
    }

    /**
     * Get all documents shared with a user
     * @param userId the user ID
     * @return list of documents
     */
    public List<CollaborativeDocument> getDocumentsSharedWithUser(String userId) {
        var user = userService.findById(userId);
        return documentRepository.findAllByIdIn(user.getSharedDocumentIds());
    }

    /**
     * Get document version history
     * @param documentId the document ID
     * @return list of document versions
     */
    public List<DocumentVersion> getDocumentVersionHistory(String documentId) {
        CollaborativeDocument document = getDocumentById(documentId);
        return document.getVersions();
    }

    /**
     * Check if a user has edit access to a document
     * @param document the document
     * @param userId the user ID
     * @return true if user has edit access
     */
    private boolean hasEditAccess(CollaborativeDocument document, String userId) {
        String role = document.getAccessRoles().get(userId);
        return userId.equals(document.getOwnerId()) || "editor".equals(role);
    }
}
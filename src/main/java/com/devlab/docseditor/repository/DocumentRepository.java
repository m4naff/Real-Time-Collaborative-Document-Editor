package com.devlab.docseditor.repository;

import com.devlab.docseditor.model.entity.CollaborativeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface DocumentRepository extends MongoRepository<CollaborativeDocument, String> {
    
    /**
     * Find all documents owned by a specific user
     * @param ownerId the ID of the owner
     * @return list of documents owned by the user
     */
    List<CollaborativeDocument> findByOwnerId(String ownerId);

    /**
     * Find all documents where a user has access (either as viewer or editor)
     * @param documentIds set of document IDs
     * @return list of documents the user has access to
     */
    List<CollaborativeDocument> findAllByIdIn(Set<String> documentIds);
    
    /**
     * Find documents by title containing the given text (case-insensitive)
     * @param titleText the text to search for in titles
     * @return list of documents with matching titles
     */
    List<CollaborativeDocument> findByTitleContainingIgnoreCase(String titleText);
}
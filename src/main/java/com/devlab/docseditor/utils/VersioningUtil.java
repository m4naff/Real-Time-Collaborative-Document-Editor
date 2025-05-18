package com.devlab.docseditor.utils;

import com.devlab.docseditor.model.entity.CollaborativeDocument;
import com.devlab.docseditor.model.entity.DocumentVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class VersioningUtil {

    /**
     * Creates a new version of the document
     * @param document the document to version
     * @param newContent the new content
     * @param userId the ID of the user making the change
     * @return the new version
     */
    public static DocumentVersion createNewVersion(CollaborativeDocument document, String newContent, String userId) {
        DocumentVersion newVersion = DocumentVersion.builder()
                .contentSnapshot(newContent)
                .editedByUserId(userId)
                .timestamp(LocalDateTime.now())
                .build();
        
        document.getVersions().add(newVersion);
        return newVersion;
    }
    
    /**
     * Gets the latest version of a document
     * @param document the document
     * @return the latest version, or empty if no versions exist
     */
    public Optional<DocumentVersion> getLatestVersion(CollaborativeDocument document) {
        List<DocumentVersion> versions = document.getVersions();
        if (versions == null || versions.isEmpty()) {
            return Optional.empty();
        }
        
        return versions.stream()
                .max(Comparator.comparing(DocumentVersion::getTimestamp));
    }
    
    /**
     * Gets a specific version of a document by index
     * @param document the document
     * @param versionIndex the index of the version (0 is the oldest)
     * @return the version at the specified index, or empty if index is out of bounds
     */
    public Optional<DocumentVersion> getVersionByIndex(CollaborativeDocument document, int versionIndex) {
        List<DocumentVersion> versions = document.getVersions();
        if (versions == null || versionIndex < 0 || versionIndex >= versions.size()) {
            return Optional.empty();
        }

        List<DocumentVersion> sortedVersions = versions.stream()
                .sorted(Comparator.comparing(DocumentVersion::getTimestamp))
                .toList();
        
        return Optional.of(sortedVersions.get(versionIndex));
    }
    
    /**
     * Calculates the number of changes between two versions
     * @param oldContent the old content
     * @param newContent the new content
     * @return the number of character differences
     */
    public int calculateChangeSize(String oldContent, String newContent) {
        if (oldContent == null) oldContent = "";
        if (newContent == null) newContent = "";

        int maxLength = Math.max(oldContent.length(), newContent.length());
        int differences = 0;
        
        for (int i = 0; i < maxLength; i++) {
            char oldChar = i < oldContent.length() ? oldContent.charAt(i) : '\0';
            char newChar = i < newContent.length() ? newContent.charAt(i) : '\0';
            
            if (oldChar != newChar) {
                differences++;
            }
        }
        
        return differences;
    }
}
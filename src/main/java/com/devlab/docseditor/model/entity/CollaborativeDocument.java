package com.devlab.docseditor.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "documents")
public class CollaborativeDocument {

    @Id
    private String id;

    private String title;
    private String content;

    private String ownerId;

    private Map<String, String> accessRoles; // key: userId, value: "viewer" or "editor"

    private List<DocumentVersion> versions; // embedded version history

}

package com.devlab.docseditor.model.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentVersion {

    private String contentSnapshot;
    private String editedByUserId;
    private LocalDateTime timestamp;

}

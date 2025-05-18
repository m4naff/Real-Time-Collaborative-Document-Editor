package com.devlab.docseditor.model.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentUpdateMessage {
    private String documentId;
    private String content;
    private String userId;
    private long timestamp;
}
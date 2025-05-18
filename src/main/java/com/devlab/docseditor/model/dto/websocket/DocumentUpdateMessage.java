package com.devlab.docseditor.model.dto.websocket;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Message for real-time document updates via WebSocket")
public class DocumentUpdateMessage {
    @Schema(description = "Unique identifier of the document", example = "60c72b2f5e7c2a1b3c9d8e7f")
    private String documentId;

    @Schema(description = "Current content of the document", example = "This is the updated document content.")
    private String content;

    @Schema(description = "ID of the user who made the update", example = "60c72b2f5e7c2a1b3c9d8e7a")
    private String userId;

    @Schema(description = "Timestamp of the update in milliseconds since epoch", example = "1623456789000")
    private long timestamp;
}

package com.devlab.docseditor.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for creating a new document")
public class CreateDocumentRequest {

    @NotBlank(message = "Title is required")
    @Schema(description = "Title of the document", example = "Meeting Notes", required = true)
    private String title;

    @Schema(description = "Initial content of the document", example = "# Meeting Notes\n\nDiscussion points:", required = false)
    private String content;
}

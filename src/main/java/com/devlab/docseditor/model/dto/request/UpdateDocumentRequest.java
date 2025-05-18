package com.devlab.docseditor.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for updating a document's content")
public class UpdateDocumentRequest {

    @NotNull(message = "Content is required")
    @Schema(description = "New content of the document", 
            example = "# Updated Meeting Notes\n\nRevised discussion points and action items.", 
            required = true)
    private String content;
}

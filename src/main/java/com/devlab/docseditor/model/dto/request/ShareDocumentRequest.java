package com.devlab.docseditor.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for sharing a document with another user")
public class ShareDocumentRequest {

    @NotBlank(message = "Target user ID is required")
    @Schema(description = "ID of the user to share the document with", 
            example = "60c72b2f5e7c2a1b3c9d8e7a", 
            required = true)
    private String targetUserId;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "viewer|editor", message = "Role must be either 'viewer' or 'editor'")
    @Schema(description = "Access role for the target user", 
            example = "editor", 
            allowableValues = {"viewer", "editor"}, 
            required = true)
    private String role;
}

package com.devlab.docseditor.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShareDocumentRequest {
    
    @NotBlank(message = "Target user ID is required")
    private String targetUserId;
    
    @NotBlank(message = "Role is required")
    @Pattern(regexp = "viewer|editor", message = "Role must be either 'viewer' or 'editor'")
    private String role;
}
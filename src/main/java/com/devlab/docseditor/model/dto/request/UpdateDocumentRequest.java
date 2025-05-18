package com.devlab.docseditor.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDocumentRequest {
    
    @NotNull(message = "Content is required")
    private String content;
}
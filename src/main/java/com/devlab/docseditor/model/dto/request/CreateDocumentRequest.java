package com.devlab.docseditor.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDocumentRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String content;
}
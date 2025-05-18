package com.devlab.docseditor.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request for user registration")
public class RegisterRequest {
    @Schema(description = "Username for the new account", example = "johndoe", required = true)
    private String username;

    @Schema(description = "Email address for the new account", example = "john.doe@example.com", required = true)
    private String email;

    @Schema(description = "Password for the new account", example = "securePassword123", required = true)
    private String password;
}

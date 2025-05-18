package com.devlab.docseditor.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request for user authentication")
public class AuthenticationRequest {

    @Schema(description = "Username for authentication", example = "johndoe", required = true)
    private String username;

    @Schema(description = "User password", example = "password123", required = true)
    private String password;

}

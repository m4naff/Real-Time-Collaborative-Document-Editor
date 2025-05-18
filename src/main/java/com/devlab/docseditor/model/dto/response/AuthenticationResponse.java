package com.devlab.docseditor.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Response containing authentication tokens")
public class AuthenticationResponse {

    @Schema(description = "JWT access token for API authentication", 
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", 
            required = true)
    private String accessToken;

    @Schema(description = "JWT refresh token for obtaining a new access token", 
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", 
            required = true)
    private String refreshToken;

}

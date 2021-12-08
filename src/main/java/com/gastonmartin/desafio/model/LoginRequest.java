package com.gastonmartin.desafio.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginRequest {
    @Schema(example = "scott", required = true, description = "Username for authentication in the system")
    private String username;
    @Schema(example = "tiger", required = true, description = "Password for authentication in the system")
    private String password;
}

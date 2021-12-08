package com.gastonmartin.desafio.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserCreationRequest {
    @Schema(example = "scott", required = true, description = "Username for signing up in the system")
    private String username;
    @Schema(example = "tiger", required = true, description = "Username for signing up in the system")
    private String password;
}

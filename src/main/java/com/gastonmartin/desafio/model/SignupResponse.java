package com.gastonmartin.desafio.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SignupResponse {
    @Schema(example = "scott", description = "Username used for signing up")
    private String userId;
    @Schema(example = "created", description = "Result of signup operation")
    private String result;
}

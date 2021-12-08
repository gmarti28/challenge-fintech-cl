package com.gastonmartin.desafio.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AdditionResponse {
    @Schema(example = "2.0", description = "Result of addition operation")
    private double result;
}

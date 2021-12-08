package com.gastonmartin.desafio.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AdditionRequest {
    @Schema(example = "1.0", required = true, description = "Left operand of addition")
    private double left;
    @Schema(example = "2.1", required = true, description = "Right operand of addition")
    private double right;
}

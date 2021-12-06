package com.gastonmartin.desafio.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SignupResponse {
    private String userId;
    private String result;
}

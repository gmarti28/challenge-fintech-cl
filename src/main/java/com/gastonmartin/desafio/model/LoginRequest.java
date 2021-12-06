package com.gastonmartin.desafio.model;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}

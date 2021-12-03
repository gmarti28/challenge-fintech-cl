package com.gastonmartin.desafio.model;

import lombok.Data;

@Data
public class UserCreationRequest {
    private String username;
    private String password;
}

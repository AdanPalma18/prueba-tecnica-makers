package com.prueba_tecnica.prestamos.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDto {

    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private List<String> roles;
}

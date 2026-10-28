package com.prueba_tecnica.prestamos.controller;

import com.prueba_tecnica.prestamos.dto.ApiResponse;
import com.prueba_tecnica.prestamos.dto.LoginRequestDto;
import com.prueba_tecnica.prestamos.dto.LoginResponseDto;
import com.prueba_tecnica.prestamos.dto.RegisterRequestDto;
import com.prueba_tecnica.prestamos.model.User;
import com.prueba_tecnica.prestamos.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> iniciarSesion(@Valid @RequestBody LoginRequestDto dto) {
        return ResponseEntity.ok(authService.iniciarSesion(dto));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> registrar(@Valid @RequestBody RegisterRequestDto dto) {
        User nuevoUsuario = authService.registrarUsuario(dto);

        nuevoUsuario.setPassword(null);

        ApiResponse<User> response = new ApiResponse<>(
                true,
                "Usuario registrado exitosamente con el correo: " + nuevoUsuario.getEmail(),
                nuevoUsuario);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
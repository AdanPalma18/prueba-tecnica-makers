package com.prueba_tecnica.prestamos.service;

import com.prueba_tecnica.prestamos.dto.RegisterRequestDto;
import com.prueba_tecnica.prestamos.model.Role;
import com.prueba_tecnica.prestamos.model.User;
import com.prueba_tecnica.prestamos.repository.RoleRepository;
import com.prueba_tecnica.prestamos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import com.prueba_tecnica.prestamos.dto.LoginRequestDto;
import com.prueba_tecnica.prestamos.dto.LoginResponseDto;
import com.prueba_tecnica.prestamos.security.JwtTokenProvider;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public LoginResponseDto iniciarSesion(LoginRequestDto dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

        String token = jwtTokenProvider.generateToken(authentication.getName());
        List<String> roles = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .toList();
        return new LoginResponseDto(token, "Bearer", jwtTokenProvider.getExpirationMs(), roles);
    }

    @Transactional
    public User registrarUsuario(RegisterRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("El correo ya está registrado: " + dto.getEmail());
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        // Asignar rol por defecto (ROLE_USER)
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Rol por defecto no encontrado en la BD"));

        user.setRoles(Collections.singleton(userRole));

        return userRepository.save(user);
    }
}
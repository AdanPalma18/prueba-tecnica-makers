package com.prueba_tecnica.prestamos.service;

import com.prueba_tecnica.prestamos.dto.LoanRequestDto;
import com.prueba_tecnica.prestamos.model.Loan;
import com.prueba_tecnica.prestamos.model.LoanStatus;
import com.prueba_tecnica.prestamos.model.User;
import com.prueba_tecnica.prestamos.repository.LoanRepository;
import com.prueba_tecnica.prestamos.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LoanService loanService;

    @Test
    void testCrearPrestamoExitoso() {
        String email = "cliente@example.com";
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail(email);

        LoanRequestDto dto = new LoanRequestDto();
        dto.setAmount(5000.0);
        dto.setTerm(12);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Loan resultado = loanService.crearPrestamo(dto, email);

        assertNotNull(resultado);
        assertEquals(5000.0, resultado.getAmount());
        assertEquals(12, resultado.getTerm());
        assertEquals(LoanStatus.PENDING, resultado.getStatus());
        assertEquals(mockUser, resultado.getUser());

        verify(userRepository, times(1)).findByEmail(email);
        verify(loanRepository, times(1)).save(any(Loan.class));
    }
}
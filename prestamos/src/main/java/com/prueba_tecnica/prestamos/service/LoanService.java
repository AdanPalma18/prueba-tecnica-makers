package com.prueba_tecnica.prestamos.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prueba_tecnica.prestamos.dto.LoanRequestDto;
import com.prueba_tecnica.prestamos.model.Loan;
import com.prueba_tecnica.prestamos.model.LoanStatus;
import com.prueba_tecnica.prestamos.model.User;
import com.prueba_tecnica.prestamos.repository.LoanRepository;
import com.prueba_tecnica.prestamos.repository.UserRepository;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @CacheEvict(value = "loansCache", allEntries = true) // limpiamos la cache al crear uno nuevo
    public Loan crearPrestamo(LoanRequestDto dto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + email));

        Loan loan = new Loan();
        loan.setAmount(dto.getAmount());
        loan.setTerm(dto.getTerm());
        loan.setStatus(LoanStatus.PENDING);
        loan.setStartDate(LocalDate.now());
        loan.setUser(user);

        return loanRepository.save(loan);
    }

    // usamos cache para optimizar consultas repetitivas
    @Cacheable(value = "loansCache", key = "#email + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<Loan> obtenerPrestamosPorUsuario(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + email));
        return loanRepository.findByUserId(user.getId(), pageable);
    }

    // solo Administrador
    public Page<Loan> obtenerTodos(Pageable pageable) {
        return loanRepository.findAll(pageable);
    }

    @Transactional
    @CacheEvict(value = "loansCache", allEntries = true) // invalidamos la cache para que se refresquen los estados
    public Loan actualizarEstado(Long loanId, LoanStatus nuevoEstado) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado con ID: " + loanId));

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new IllegalStateException(
                    "El préstamo ya fue procesado previamente y su estado es: " + loan.getStatus());
        }

        loan.setStatus(nuevoEstado);
        return loanRepository.save(loan);
    }
}
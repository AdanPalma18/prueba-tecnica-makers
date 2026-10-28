package com.prueba_tecnica.prestamos.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prueba_tecnica.prestamos.dto.ApiResponse;
import com.prueba_tecnica.prestamos.dto.LoanRequestDto;
import com.prueba_tecnica.prestamos.dto.StatusUpdateDto;
import com.prueba_tecnica.prestamos.model.Loan;
import com.prueba_tecnica.prestamos.service.LoanService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @PostMapping
    public ResponseEntity<ApiResponse<Loan>> solicitarPrestamo(@Valid @RequestBody LoanRequestDto dto,
            Principal principal) {
        Loan nuevoLoan = loanService.crearPrestamo(dto, principal.getName());

        ApiResponse<Loan> response = new ApiResponse<>(
                true,
                "Préstamo solicitado exitosamente",
                nuevoLoan);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<Loan>> obtenerMisPrestamos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("startDate").descending());
        Page<Loan> loans = loanService.obtenerPrestamosPorUsuario(principal.getName(), pageable);

        return ResponseEntity.ok(loans);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Loan>> obtenerTodosLosPrestamos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("startDate").descending());
        return ResponseEntity.ok(loanService.obtenerTodos(pageable));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Loan>> cambiarEstadoPrestamo(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateDto dto) {

        Loan loanActualizado = loanService.actualizarEstado(id, dto.getStatus());
        return ResponseEntity
                .ok(new ApiResponse<>(true, "Estado del préstamo actualizado correctamente", loanActualizado));
    }
}
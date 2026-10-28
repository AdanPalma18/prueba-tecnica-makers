package com.prueba_tecnica.prestamos.dto;

import com.prueba_tecnica.prestamos.model.LoanStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusUpdateDto {
    @NotNull(message = "El nuevo estado es obligatorio")
    private LoanStatus status; // APPROVED o REJECTED
}
package com.prueba_tecnica.prestamos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoanRequestDto {
    @NotNull(message = "El monto es obligatorio")
    @Min(value = 100, message = "El monto minimo del prestamo es 100")
    private Double amount;

    @NotNull(message = "El plazo es obligatorio")
    @Min(value = 1, message = "El plazo minimo es de 1 mes")
    private Integer term;
}
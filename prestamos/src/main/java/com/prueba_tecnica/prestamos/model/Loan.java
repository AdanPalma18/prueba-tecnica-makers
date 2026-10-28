package com.prueba_tecnica.prestamos.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "prestamos")
@Data
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double amount; // Monto del prestamo

    @Column(nullable = false)
    private Integer term; // Plazo en meses

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status = LoanStatus.PENDING; // Por defecto tiene estado pendiente

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate = LocalDate.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // al ser un plazo en meses, se puede calcular la fecha de finalización sumando
    // el plazo a la fecha de inicio
    // por lo que ademas no es necesario que nosotros pongamos una fecha de
    // finalizacion
    public LocalDate getEndDate() {
        if (this.startDate != null && this.term != null) {
            return this.startDate.plusMonths(this.term);
        }
        return null;
    }
}
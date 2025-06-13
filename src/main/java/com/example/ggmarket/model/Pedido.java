package com.example.ggmarket.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private String estado; // "PROCESANDO", "COMPLETADO", "CANCELADO"

    @Column(nullable = false)
    private BigDecimal total;

    // Relación One-to-Many: Un pedido tiene muchos detalles (líneas de producto).
    // cascade = CascadeType.ALL: Si guardamos/eliminamos un Pedido, se guardan/eliminan sus detalles.
    // orphanRemoval = true: Si quitamos un detalle de la lista, se elimina de la BD.
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> detalles = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.estado = "PROCESANDO";
    }
}
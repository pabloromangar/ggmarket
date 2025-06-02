package com.example.ggmarket.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "carrito")
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private ProductoFisico productoFisico;

    private Integer cantidad;

    @Enumerated(EnumType.STRING)
    private TipoProducto tipoProducto;

    private String fechaAgregado;

    // Getters and setters
}

enum TipoProducto {
    DIGITAL,
    FISICO
}

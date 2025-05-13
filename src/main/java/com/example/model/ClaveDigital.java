package com.example.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "claves_digitales")
public class ClaveDigital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clave;

    @ManyToOne
    @JoinColumn(name = "producto_digital_id")
    private ProductoDigital productoDigital;

    private String estado; // disponible, vendido
}

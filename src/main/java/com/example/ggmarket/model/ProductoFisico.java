package com.example.ggmarket.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "productos_fisicos")
public class ProductoFisico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private Double precio;

    @ManyToOne
    @JoinColumn(name = "vendedor_id")
    private Usuario vendedor;
}

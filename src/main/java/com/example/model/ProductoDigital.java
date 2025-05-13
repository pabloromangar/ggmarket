package com.example.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "productos_digitales")
public class ProductoDigital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer stock;

    @OneToMany(mappedBy = "productoDigital")
    private List<ClaveDigital> claves;

    // Getters and setters
}

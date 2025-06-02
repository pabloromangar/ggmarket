package com.example.ggmarket.model;

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
    private String categoria;
    @Column(name = "imagen_url")
    private String imagenUrl;
    @OneToMany(mappedBy = "productoDigital")
    private List<ClaveDigital> claves;

    
}

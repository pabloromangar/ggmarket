package com.example.ggmarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data 
@AllArgsConstructor
public class CarritoItemDTO {
    private Long productoId;
    private String nombre;
    private String imagenUrl;
    private int cantidad;
    private double precio;
    private String vendedor; // O cualquier otro dato que quieras mostrar
}
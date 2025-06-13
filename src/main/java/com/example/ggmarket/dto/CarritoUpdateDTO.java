package com.example.ggmarket.dto;

import lombok.Data;

@Data // Lombok para getters y setters
public class CarritoUpdateDTO {

    // Los nombres de los campos DEBEN coincidir EXACTAMENTE con las claves del JSON
    private Long productId;
    private Integer nuevaCantidad; // Usamos Integer para que pueda ser null
}
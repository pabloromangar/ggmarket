package com.example.ggmarket.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private ProductoFisico productoFisico;

    private String tipoProducto;  // "digital" o "fisico"
    private String estado;  // "pendiente", "pagado", "enviado"

    private String fecha;

    // Getters and setters
}

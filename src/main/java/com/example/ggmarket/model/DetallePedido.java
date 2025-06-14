package com.example.ggmarket.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
@Table(name = "detalles_pedido")
public class DetallePedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // LA CONEXIÓN INVERSA: Muchos detalles pertenecen a un solo Pedido.
    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "producto_digital_id", nullable = true)
    private ProductoDigital productoDigital; // Qué producto se compró

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_fisico_id", nullable = true)
    private ProductoFisico productoFisico; // <-- NUEVO CAMPO
    private int cantidad;
    private BigDecimal precioUnitario; // A qué precio se compró
    @OneToOne
    @JoinColumn(name = "clave_digital_id", unique = true) // <-- PROBLEMA #2 (sutil)
    private ClaveDigital claveDigital;

    public String getNombreProducto() {
        if (productoDigital != null) {
            return productoDigital.getNombre();
        }
        if (productoFisico != null) {
            return productoFisico.getNombre();
        }
        return "Producto no disponible";
    }
}
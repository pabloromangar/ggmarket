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
    @JoinColumn(name = "producto_digital_id", nullable = false)
    private ProductoDigital productoDigital; // Qué producto se compró

    private int cantidad;
    private BigDecimal precioUnitario; // A qué precio se compró
    @OneToOne // Una línea de pedido se corresponde con UNA única clave vendida.
    @JoinColumn(name = "clave_digital_id", unique = true) // <-- ¡AQUÍ ESTÁ LA MAGIA!
    private ClaveDigital claveDigital;
    // ... (constructores, getters, setters)
}
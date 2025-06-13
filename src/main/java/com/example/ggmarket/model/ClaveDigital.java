package com.example.ggmarket.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "claves_digitales")
public class ClaveDigital {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clave; // El texto "ABCD-1234..."
    private boolean usada;

    @ManyToOne
    @JoinColumn(name = "producto_digital_id")
    private ProductoDigital productoDigital;
    // Dentro de la clase ClaveDigital
    @OneToOne(mappedBy = "claveDigital")
    private DetallePedido detallePedido;
}

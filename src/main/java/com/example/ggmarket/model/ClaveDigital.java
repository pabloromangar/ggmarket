package com.example.ggmarket.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "claves_digitales")
@Data
public class ClaveDigital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clave;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_digital_id")
    private ProductoDigital productoDigital;

    private boolean usada;

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public ProductoDigital getProductoDigital() {
        return productoDigital;
    }

    public void setProductoDigital(ProductoDigital productoDigital) {
        this.productoDigital = productoDigital;
    }

    public boolean isUsada() {
        return usada;
    }

    public void setUsada(boolean usada) {
        this.usada = usada;
    }
}

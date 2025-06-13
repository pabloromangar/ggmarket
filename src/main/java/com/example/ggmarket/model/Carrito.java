package com.example.ggmarket.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Representa un ítem en el carrito de compras de un usuario.
 * Cada instancia de esta clase es una fila en la tabla 'carrito'
 * que asocia un Usuario con un ProductoDigital y una cantidad.
 */
@Data // Genera automáticamente getters, setters, toString, etc.
@NoArgsConstructor // Genera un constructor vacío, requerido por JPA/Hibernate.
@Entity // Marca esta clase como una entidad JPA que se mapeará a una tabla.
@Table(name = "carrito") // Especifica el nombre de la tabla en la base de datos.
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // El ID se auto-genera y se auto-incrementa.
    private Long id;

    /**
     * El usuario al que pertenece este ítem del carrito.
     * La relación es Many-to-One porque muchos ítems del carrito pueden pertenecer a un solo usuario.
     * FetchType.LAZY es una optimización: el Usuario no se carga de la BBDD a menos que se acceda a él explícitamente.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false) // Define la columna de clave foránea.
    private Usuario usuario;

    /**
     * El producto digital que se ha añadido al carrito.
     * La relación es Many-to-One porque el mismo producto puede estar en los carritos de muchos usuarios.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_digital_id", nullable = false) // Columna de clave foránea para el producto.
    private ProductoDigital productoDigital;

    /**
     * La cantidad de este producto específico que el usuario ha añadido al carrito.
     */
    @Column(nullable = false)
    private Integer cantidad;

    /**
     * La fecha y hora en que el ítem fue añadido o actualizado por última vez.
     * Se establece automáticamente al crear una nueva instancia.
     */
    private LocalDateTime fechaAgregado;

    /**
     * Constructor con argumentos para facilitar la creación de nuevos ítems de carrito.
     * @param usuario El usuario propietario del carrito.
     * @param productoDigital El producto que se añade.
     * @param cantidad La cantidad inicial del producto.
     */
    public Carrito(Usuario usuario, ProductoDigital productoDigital, Integer cantidad) {
        this.usuario = usuario;
        this.productoDigital = productoDigital;
        this.cantidad = cantidad;
        this.fechaAgregado = LocalDateTime.now(); // La fecha se establece al momento de la creación.
    }
}
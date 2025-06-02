package com.example.ggmarket.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Rol rol;  // Enum de roles (CLIENTE, VENDEDOR, ADMIN)

    private String fechaRegistro;

    @OneToMany(mappedBy = "usuario")
    private List<Pedido> pedidos;
}

enum Rol {
    CLIENTE,
    VENDEDOR,
    ADMIN
}

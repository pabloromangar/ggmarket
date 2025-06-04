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

    @ManyToOne
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    private String fechaRegistro;

    @OneToMany(mappedBy = "usuario")
    private List<Pedido> pedidos;
}

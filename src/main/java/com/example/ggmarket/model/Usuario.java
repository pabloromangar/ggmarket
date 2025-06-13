package com.example.ggmarket.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor 
@Table(name = "usuarios")
public class Usuario {

   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String email;
    private String password;

   @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "rol_id", referencedColumnName = "id")
    private Rol rol;
public Usuario(String nombre, String email, String password, Rol rol) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    // Getters y Setters para 'rol'
    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    @OneToMany(mappedBy = "usuario")
    private List<Pedido> pedidos;
}

package com.example.ggmarket.repository;

import com.example.ggmarket.model.Carrito;
import com.example.ggmarket.model.ProductoDigital; // <-- Cambio
import com.example.ggmarket.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    // El método ahora busca por ProductoDigital
    Optional<Carrito> findByUsuarioAndProductoDigital(Usuario usuario, ProductoDigital productoDigital);

    int countByUsuario_Email(String email);

    List<Carrito> findByUsuario_Email(String email);

    List<Carrito> findByUsuario(Usuario usuario);
}
package com.example.ggmarket.repository;

import com.example.ggmarket.model.ProductoFisico;
import com.example.ggmarket.model.Usuario; // Importante
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // Importante

@Repository
public interface ProductoFisicoRepository extends JpaRepository<ProductoFisico, Long> {

    /**
     * Busca todos los productos físicos listados por un vendedor específico,
     * ordenados por ID para consistencia.
     */
    List<ProductoFisico> findByVendedorOrderByIdDesc(Usuario vendedor);
}
package com.example.ggmarket.repository;

import com.example.ggmarket.model.ProductoDigital;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoDigitalRepository extends JpaRepository<ProductoDigital, Long> {
    List<ProductoDigital> findByTipoContainingIgnoreCase(String tipo);

    List<ProductoDigital> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Devuelve una lista de los X productos más baratos.
     * Pageable nos permite limitar el resultado (ej. solo los 8 primeros).
     */
    List<ProductoDigital> findByOrderByPrecioAsc(PageRequest pageable);

    /**
     * Devuelve una lista de los X productos de un tipo específico (ej. "TARJETA").
     * Asumiendo que tu entidad ProductoDigital tiene un campo 'tipo'.
     */
    List<ProductoDigital> findByTipoOrderByNombreAsc(String tipo, PageRequest pageable);

        Page<ProductoDigital> findByTipoIgnoreCase(String tipo, Pageable pageable);
            Page<ProductoDigital> findByPlataformaIgnoreCase(String plataforma, Pageable pageable);
}

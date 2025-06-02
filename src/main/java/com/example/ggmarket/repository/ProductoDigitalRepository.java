package com.example.ggmarket.repository;

import com.example.ggmarket.model.ProductoDigital;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoDigitalRepository extends JpaRepository<ProductoDigital, Long> {
    List<ProductoDigital> findByCategoriaContainingIgnoreCase(String categoria);
    List<ProductoDigital> findByNombreContainingIgnoreCase(String nombre);

}

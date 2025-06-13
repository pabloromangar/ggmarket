package com.example.ggmarket.repository;

import com.example.ggmarket.model.ClaveDigital;
import com.example.ggmarket.model.ProductoDigital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClaveDigitalRepository extends JpaRepository<ClaveDigital, Long> {

    List<ClaveDigital> findByProductoDigital(ProductoDigital producto);

    long countByProductoDigitalAndUsadaFalse(ProductoDigital producto);

    Optional<ClaveDigital> findTopByProductoDigitalAndUsadaIsFalse(ProductoDigital productoDigital);
}

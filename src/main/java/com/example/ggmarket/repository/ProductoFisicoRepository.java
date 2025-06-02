package com.example.ggmarket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.ggmarket.model.ProductoFisico;

public interface ProductoFisicoRepository extends JpaRepository<ProductoFisico, Long> {
    
}

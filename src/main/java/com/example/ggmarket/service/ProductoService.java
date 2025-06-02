package com.example.ggmarket.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ggmarket.model.ProductoDigital;
import com.example.ggmarket.model.ProductoFisico;
import com.example.ggmarket.repository.ProductoDigitalRepository;
import com.example.ggmarket.repository.ProductoFisicoRepository;

@Service
public class ProductoService {
     @Autowired
    private ProductoDigitalRepository digitalRepo;

    @Autowired
    private ProductoFisicoRepository fisicoRepo;

    // Digitales
    public List<ProductoDigital> listarProductosDigitales() {
        return digitalRepo.findAll();
    }

    public ProductoDigital guardarProductoDigital(ProductoDigital producto) {
        return digitalRepo.save(producto);
    }

    public ProductoDigital obtenerProductoDigital(Long id) {
        return digitalRepo.findById(id).orElse(null);
    }

    public void eliminarProductoDigital(Long id) {
        digitalRepo.deleteById(id);
    }

    // Físicos
    public List<ProductoFisico> listarProductosFisicos() {
        return fisicoRepo.findAll();
    }

    public ProductoFisico guardarProductoFisico(ProductoFisico producto) {
        return fisicoRepo.save(producto);
    }

    public ProductoFisico obtenerProductoFisico(Long id) {
        return fisicoRepo.findById(id).orElse(null);
    }

    public void eliminarProductoFisico(Long id) {
        fisicoRepo.deleteById(id);
    }
}

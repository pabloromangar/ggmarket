package com.example.ggmarket.service;

import com.example.ggmarket.model.ProductoDigital;
import com.example.ggmarket.repository.ProductoDigitalRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoDigitalService {

    @Autowired
    private ProductoDigitalRepository repo;

    @Autowired
    private ImagenJuegoService imagenJuegoService;



    public ProductoDigital crearProducto(ProductoDigital producto) {
        if (producto.getImagenUrl() == null || producto.getImagenUrl().isEmpty()) {
            String imagen = imagenJuegoService.obtenerImagenPorNombre(producto.getNombre());
            if (imagen != null) {
                producto.setImagenUrl(imagen);
            }
        }
        return repo.save(producto);
    }

    public Page<ProductoDigital> obtenerProductos(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public List<ProductoDigital> findAll() {
        return repo.findAll();
    }

    public Optional<ProductoDigital> findById(Long id) {
        return repo.findById(id);
    }

    public ProductoDigital save(ProductoDigital producto) {
        return repo.save(producto);
    }

    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    public List<ProductoDigital> findByNombre(String nombre) {
        return repo.findByNombreContainingIgnoreCase(nombre);
    }

    public List<ProductoDigital> findByCategoria(String categoria) {
        return repo.findByCategoriaContainingIgnoreCase(categoria);
    }

    public List<ProductoDigital> buscarPorNombre(String nombre) {
        return repo.findByNombreContainingIgnoreCase(nombre);
    }
}

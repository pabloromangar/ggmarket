package com.example.ggmarket.service;

import com.example.ggmarket.model.ProductoDigital;
import com.example.ggmarket.repository.ProductoDigitalRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoDigitalService {

    @Autowired
    private ProductoDigitalRepository repo;

    @Autowired
    private IGDBService imagenJuegoService;

    public ProductoDigital crearProducto(ProductoDigital producto) {
        if (producto.getImagenUrl() == null || producto.getImagenUrl().isEmpty()) {
            String imagen = imagenJuegoService.getCoverUrl(producto.getNombre());
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

    public ProductoDigital findById(Long id) {
        ProductoDigital producto = repo.findById(id).orElse(null);
        return producto;
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

    public List<ProductoDigital> findByTipo(String tipo) {
        return repo.findByTipoContainingIgnoreCase(tipo);
    }

    public List<ProductoDigital> buscarPorNombre(String nombre) {
        return repo.findByNombreContainingIgnoreCase(nombre);
    }
}

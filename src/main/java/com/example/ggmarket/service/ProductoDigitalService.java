package com.example.ggmarket.service;

import com.example.ggmarket.model.ProductoDigital;
import com.example.ggmarket.repository.ProductoDigitalRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductoDigitalService {

    @Autowired
    private ProductoDigitalRepository repo;

    @Autowired
    private IGDBService igdbService;

    @Autowired
    private CloudinaryService cloudinaryService;
    
     public ProductoDigital saveOrUpdateWithImage(ProductoDigital producto, MultipartFile imagenFile) throws IOException {
        
        // --- LÓGICA DE GESTIÓN DE IMAGEN ---
        
        // 1. Prioridad #1: Si el administrador sube un archivo nuevo.
        if (imagenFile != null && !imagenFile.isEmpty()) {
            // Subimos el archivo a Cloudinary y obtenemos la URL.
            String imageUrl = cloudinaryService.uploadFile(imagenFile);
            // Asignamos la nueva URL al producto.
            producto.setImagenUrl(imageUrl);
        } 
        // 2. Prioridad #2: Si NO se sube archivo Y es un producto NUEVO (sin ID), intentamos usar IGDB.
        else if (producto.getId() == null && (producto.getImagenUrl() == null || producto.getImagenUrl().isBlank())) {
            // Si no se proporcionó un archivo y es un producto nuevo, usamos el fallback a IGDB.
            String imagenDesdeIGDB = igdbService.getCoverUrl(producto.getNombre());
            if (imagenDesdeIGDB != null) {
                producto.setImagenUrl(imagenDesdeIGDB);
            }
        }
        // 3. Si es una edición y no se sube un nuevo archivo, simplemente se conserva la URL existente.

        // Finalmente, guardamos el producto en la base de datos.
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

      /**
     * MÉTODO NUEVO: Obtiene los N productos más baratos.
     */
    public List<ProductoDigital> findJuegosBaratos(int limit) {
        return repo.findByOrderByPrecioAsc(PageRequest.of(0, limit));
    }

    /**
     * MÉTODO NUEVO: Obtiene los N productos de un tipo específico.
     */
    public List<ProductoDigital> findPorTipo(String tipo, int limit) {
        return repo.findByTipoOrderByNombreAsc(tipo, PageRequest.of(0, limit));
    }
}

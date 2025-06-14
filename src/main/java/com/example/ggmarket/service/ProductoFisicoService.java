package com.example.ggmarket.service;

import com.example.ggmarket.model.ProductoFisico;
import com.example.ggmarket.model.Usuario;
import com.example.ggmarket.repository.ProductoFisicoRepository;
import com.example.ggmarket.repository.UsuarioRepository;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoFisicoService {

    private final ProductoFisicoRepository productoFisicoRepository;
    private final UsuarioRepository usuarioRepository;

    public ProductoFisicoService(ProductoFisicoRepository productoFisicoRepository,
            UsuarioRepository usuarioRepository) {
        this.productoFisicoRepository = productoFisicoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Obtiene todos los productos listados por un vendedor.
     */
    public List<ProductoFisico> findProductosByVendedorEmail(String email) {
        Usuario vendedor = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));
        return productoFisicoRepository.findByVendedorOrderByIdDesc(vendedor);
    }

    /**
     * Guarda un nuevo producto físico. Asigna el vendedor automáticamente.
     */
    public void save(ProductoFisico producto, String vendedorEmail) {
        Usuario vendedor = usuarioRepository.findByEmail(vendedorEmail)
                .orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));
        if (producto.getImagenUrl() != null && producto.getImagenUrl().isBlank()) {
            producto.setImagenUrl(null);
        }
        producto.setVendedor(vendedor);
        productoFisicoRepository.save(producto);
    }

   public Optional<ProductoFisico> findProductoByIdAndVendedorEmail(Long productoId, String vendedorEmail) {
        // 1. Buscamos el producto por su ID. Esto devuelve un Optional<ProductoFisico>.
        return productoFisicoRepository.findById(productoId)
            // 2. Usamos el método .filter() del Optional, que es perfecto para esto.
            //    El código dentro del filter solo se ejecuta si se encontró un producto.
            //    Comprueba si el email del vendedor del producto es igual al email del usuario logueado.
            .filter(producto -> producto.getVendedor().getEmail().equals(vendedorEmail));
            // 3. Si la condición del filter es true, se devuelve el Optional con el producto.
            //    Si es false (o si el producto no se encontró), se devuelve un Optional vacío.
    }

       public void update(ProductoFisico productoForm, String vendedorEmail) {
        // Buscamos el producto existente en la BD, asegurándonos de que pertenece al vendedor.
        ProductoFisico productoExistente = findProductoByIdAndVendedorEmail(productoForm.getId(), vendedorEmail)
            .orElseThrow(() -> new AccessDeniedException("Intento de modificación de producto no autorizado."));

        // Actualizamos los campos.
        productoExistente.setNombre(productoForm.getNombre());
        productoExistente.setDescripcion(productoForm.getDescripcion());
        productoExistente.setPrecio(productoForm.getPrecio());

        // IMPORTANTE: Solo actualizamos la URL de la imagen si se proporcionó una nueva.
        // Si productoForm.getImagenUrl() es null, no hacemos nada y se conserva la antigua.
        if (productoForm.getImagenUrl() != null) {
            productoExistente.setImagenUrl(productoForm.getImagenUrl());
        }

        // Guardamos la entidad actualizada.
        productoFisicoRepository.save(productoExistente);
    }
     /**
     * ========================================================================
     *                     MÉTODO 'deleteProducto' IMPLEMENTADO
     * ========================================================================
     * Elimina un producto físico de la base de datos. Antes de eliminarlo,
     * realiza una comprobación de seguridad crucial para asegurar que el usuario
     * que solicita la eliminación es el verdadero propietario del producto.
     *
     * @param productoId El ID del producto que se desea eliminar.
     * @param vendedorEmail El email del usuario que está realizando la acción.
     * @throws RuntimeException si el producto con el ID especificado no se encuentra.
     * @throws AccessDeniedException si el usuario logueado no es el propietario del producto.
     */
    public void deleteProducto(Long productoId, String vendedorEmail) {
        
        // 1. Buscamos el producto en la base de datos por su ID.
        //    Si no se encuentra, lanzamos una excepción clara. El proceso no puede continuar.
        ProductoFisico producto = productoFisicoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productoId));

        // 2. ¡COMPROBACIÓN DE SEGURIDAD CRÍTICA!
        //    Obtenemos el email del vendedor asociado a este producto y lo comparamos
        //    con el email del usuario que ha iniciado la sesión.
        if (!producto.getVendedor().getEmail().equals(vendedorEmail)) {
            // Si los emails NO coinciden, significa que un usuario está intentando
            // borrar el producto de otro. Lanzamos una excepción de acceso denegado.
            // Spring Security puede capturar esto y mostrar una página de error 403.
            throw new AccessDeniedException("Acceso denegado. No tienes permiso para eliminar este producto.");
        }

        // 3. Si la comprobación de seguridad pasa, procedemos a eliminar el producto.
        //    El método delete() de JpaRepository se encarga de ejecutar la sentencia DELETE
        //    en la base de datos.
        productoFisicoRepository.delete(producto);
    }
}
package com.example.ggmarket.service;

import com.example.ggmarket.model.*;
import com.example.ggmarket.repository.ClaveDigitalRepository;
import com.example.ggmarket.repository.PedidoRepository;
import com.example.ggmarket.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CarritoViewService carritoViewService;
    private final CarritoService carritoService;
    private final ClaveDigitalRepository claveDigitalRepository;

    public PedidoService(PedidoRepository pedidoRepository, CarritoService carritoService,CarritoViewService carritoViewService, UsuarioRepository usuarioRepository, ClaveDigitalRepository claveDigitalRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.carritoViewService = carritoViewService;
        this.claveDigitalRepository = claveDigitalRepository;
        this.carritoService = carritoService;
    }

    public List<Pedido> findPedidosByUsuarioEmail(String emailUsuario) {
        // Primero encontramos al usuario
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado para obtener historial de pedidos"));

        // Luego usamos el nuevo método del repositorio
        return pedidoRepository.findByUsuarioOrderByFechaCreacionDesc(usuario);
    }
    /**
     * Crea un nuevo pedido a partir del carrito de un usuario, lo guarda en la BD
     * y luego limpia el carrito.
     * La anotación @Transactional asegura que toda la operación sea atómica:
     * o todo se completa con éxito, o no se hace ningún cambio en la BD.
     */
     @Transactional // ¡MUY IMPORTANTE! Si algo falla, deshace todos los cambios.
    public Pedido crearPedidoDesdeCarrito(String emailUsuario) {
        
        // 1. OBTENER DATOS INICIALES
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario).get();
        List<Carrito> itemsCarrito = carritoViewService.getItemsDelUsuario(emailUsuario);

        // 2. CREAR EL "CONTENEDOR" DEL PEDIDO
        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setUsuario(usuario);
        
        BigDecimal totalGeneral = BigDecimal.ZERO;

        // 3. PROCESAR CADA PRODUCTO DEL CARRITO
        for (Carrito itemCarrito : itemsCarrito) {
            ProductoDigital productoComprado = itemCarrito.getProductoDigital();

            // 4. POR CADA UNIDAD COMPRADA, BUSCAR Y ASIGNAR UNA CLAVE
            for (int i = 0; i < itemCarrito.getCantidad(); i++) {
                
                // 4a. Buscar en el inventario una clave disponible para este producto
                ClaveDigital claveParaVender = claveDigitalRepository
                    .findTopByProductoDigitalAndUsadaIsFalse(productoComprado)
                    .orElseThrow(() -> new RuntimeException("Stock agotado para " + productoComprado.getNombre()));

                // 4b. Marcar la clave como usada para que no se vuelva a vender
                claveParaVender.setUsada(true);

                // 4c. Crear la "línea de detalle" para esta clave específica
                DetallePedido detalle = new DetallePedido();
                detalle.setPedido(nuevoPedido); // Pertenece al pedido que estamos creando
                detalle.setProductoDigital(productoComprado);
                detalle.setCantidad(1); // Cada detalle es para 1 clave
                detalle.setPrecioUnitario(BigDecimal.valueOf(productoComprado.getPrecio()));
                
                // 4d. === VINCULAR EL DETALLE CON LA CLAVE (USANDO OBJETOS) ===
                // JPA se encargará de usar la ID por debajo.
                detalle.setClaveDigital(claveParaVender);
                
                // 4e. Añadir esta línea completa al pedido
                nuevoPedido.getDetalles().add(detalle);
                
                // 4f. Actualizar el total
                totalGeneral = totalGeneral.add(detalle.getPrecioUnitario());
            }
            
            // 5. ACTUALIZAR EL STOCK GENERAL DEL PRODUCTO
            productoComprado.setStock(productoComprado.getStock() - itemCarrito.getCantidad());
        }

        // 6. GUARDAR TODO
        nuevoPedido.setTotal(totalGeneral);
        pedidoRepository.save(nuevoPedido); // Gracias a CascadeType.ALL, esto guarda el pedido y todos sus detalles.
        
        // 7. LIMPIAR EL CARRITO
        carritoService.limpiarCarrito(emailUsuario);

        return nuevoPedido;
    }
}
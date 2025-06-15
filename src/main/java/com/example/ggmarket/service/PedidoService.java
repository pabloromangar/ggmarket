package com.example.ggmarket.service;

import com.example.ggmarket.model.*;
import com.example.ggmarket.repository.ClaveDigitalRepository;
import com.example.ggmarket.repository.PedidoRepository;
import com.example.ggmarket.repository.ProductoFisicoRepository;
import com.example.ggmarket.repository.UsuarioRepository;
import jakarta.transaction.Transactional;

import org.springframework.security.access.AccessDeniedException;
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
    private final ProductoFisicoRepository productoFisicoRepository;

    public PedidoService(PedidoRepository pedidoRepository, CarritoService carritoService,
            CarritoViewService carritoViewService, UsuarioRepository usuarioRepository,
            ClaveDigitalRepository claveDigitalRepository,
            ProductoFisicoRepository productoFisicoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.carritoViewService = carritoViewService;
        this.claveDigitalRepository = claveDigitalRepository;
        this.carritoService = carritoService;
        this.productoFisicoRepository = productoFisicoRepository;
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
                nuevoPedido.setTipoPedido("DIGITAL");
                nuevoPedido.setEstado("COMPLETADO");
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

    @Transactional
public Pedido crearPedidoParaProductoFisico(Long productoId, String emailComprador) {
    // 1. Buscamos las entidades necesarias
    Usuario comprador = usuarioRepository.findByEmail(emailComprador)
        .orElseThrow(() -> new RuntimeException("Comprador no encontrado"));

    ProductoFisico producto = productoFisicoRepository.findById(productoId)
        .orElseThrow(() -> new RuntimeException("Producto físico no encontrado"));

    // 2. Comprobación de seguridad: No se puede comprar un producto ya vendido
    if (producto.isVendido()) {
        throw new IllegalStateException("Este producto ya no está disponible porque ha sido vendido.");
    }
    
    // 3. Creamos el pedido y el detalle, como antes
    Pedido pedido = new Pedido();
    pedido.setUsuario(comprador);
    pedido.setTotal(BigDecimal.valueOf(producto.getPrecio()));
    pedido.setTipoPedido("FISICO");
    pedido.setEstado("PENDIENTE_ENVIO");

    DetallePedido detalle = new DetallePedido();
    detalle.setPedido(pedido);
    detalle.setProductoFisico(producto);
    detalle.setCantidad(1);
    detalle.setPrecioUnitario(BigDecimal.valueOf(producto.getPrecio()));

    pedido.getDetalles().add(detalle);

    // 4. Guardamos el nuevo pedido (esto lo hace persistente)
    Pedido pedidoGuardado = pedidoRepository.save(pedido);

    // 5. ========= LÓGICA CORREGIDA Y DEFINITIVA =========
    // En lugar de borrar el producto, lo marcamos como vendido.
    producto.setVendido(true);
    // Y guardamos el cambio. Como 'producto' ya tiene un ID, esto hará un UPDATE.
    productoFisicoRepository.save(producto);
    // ===================================================
    
    return pedidoGuardado;
}

    public List<Pedido> findPedidosParaVendedor(String emailVendedor) {
        Usuario vendedor = usuarioRepository.findByEmail(emailVendedor)
                .orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));
        return pedidoRepository.findDistinctByDetalles_ProductoFisico_Vendedor(vendedor);
    }

    // Cambia el estado del pedido a "ENVIADO"
    @Transactional
    public void confirmarEnvio(Long pedidoId, String emailVendedor) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        // Comprobación de seguridad
        boolean perteneceAlVendedor = pedido.getDetalles().stream()
                .anyMatch(d -> d.getProductoFisico() != null
                        && d.getProductoFisico().getVendedor().getEmail().equals(emailVendedor));

        if (!perteneceAlVendedor) {
            throw new AccessDeniedException("No tienes permiso para modificar este pedido.");
        }

        pedido.setEstado("ENVIADO");
        pedidoRepository.save(pedido);
    }
}
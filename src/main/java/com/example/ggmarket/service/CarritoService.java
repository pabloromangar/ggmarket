package com.example.ggmarket.service;

import com.example.ggmarket.model.Carrito;
import com.example.ggmarket.model.ProductoDigital;
import com.example.ggmarket.model.Usuario;
import com.example.ggmarket.repository.CarritoRepository;
import com.example.ggmarket.repository.ProductoDigitalRepository;
import com.example.ggmarket.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar la lógica de negocio relacionada con el carrito de
 * compras persistente.
 * Todas las operaciones de escritura están marcadas como transaccionales para
 * garantizar la integridad de los datos.
 */
@Service
public class CarritoService {

    // Inyección de dependencias de los repositorios necesarios.
    // Spring se encarga de proporcionar las instancias de estas interfaces.
    private final CarritoRepository carritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoDigitalRepository productoDigitalRepository;

    /**
     * Constructor para la inyección de dependencias. Es una buena práctica usar
     * inyección por constructor ya que asegura que las dependencias son finales y
     * no nulas.
     *
     * @param carritoRepository         Repositorio para acceder a los datos de la
     *                                  entidad Carrito.
     * @param usuarioRepository         Repositorio para acceder a los datos de la
     *                                  entidad Usuario.
     * @param productoDigitalRepository Repositorio para acceder a los datos de la
     *                                  entidad ProductoDigital.
     */

    public CarritoService(CarritoRepository carritoRepository,
            UsuarioRepository usuarioRepository,
            ProductoDigitalRepository productoDigitalRepository) {
        this.carritoRepository = carritoRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoDigitalRepository = productoDigitalRepository;
    }

    /**
     * Agrega un producto digital al carrito de un usuario o actualiza su cantidad
     * si ya existe.
     * La operación se realiza dentro de una transacción para asegurar que todos los
     * cambios
     * en la base de datos se completen con éxito o no se realice ninguno
     * (atomicidad).
     *
     * @param emailUsuario El email del usuario que está realizando la acción. Se
     *                     usa para identificarlo de forma única.
     * @param productoId   El ID del producto digital que se va a añadir al carrito.
     * @param cantidad     La cantidad del producto a añadir (generalmente 1).
     * @throws RuntimeException si el usuario o el producto no se encuentran en la
     *                          base de datos.
     */
    @Transactional
    public void agregarProductoDigital(String emailUsuario, Long productoId, int cantidad) {

        // --- PASO 1: Obtener las entidades de la base de datos ---
        // Buscamos al usuario por su email. Si no se encuentra, la operación no puede
        // continuar y se lanza una excepción.
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(
                        () -> new RuntimeException("Error interno: Usuario no encontrado con email: " + emailUsuario));

        // Buscamos el producto por su ID. Si no existe, se lanza una excepción.
        ProductoDigital producto = productoDigitalRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("El producto que intentas añadir ya no está disponible."));

        // --- PASO 2: Comprobar si el producto ya está en el carrito de este usuario
        // ---
        // Usamos el método personalizado del repositorio para buscar una entrada que
        // coincida con el usuario y el producto.
        Optional<Carrito> itemExistenteOpt = carritoRepository.findByUsuarioAndProductoDigital(usuario, producto);

        if (itemExistenteOpt.isPresent()) {
            // --- CASO A: El producto ya está en el carrito ---
            // Obtenemos la entrada existente del carrito.
            Carrito itemExistente = itemExistenteOpt.get();
            // Actualizamos la cantidad, sumando la nueva cantidad a la existente.
            itemExistente.setCantidad(itemExistente.getCantidad() + cantidad);
            // Guardamos la entidad actualizada. JPA se encargará de generar la consulta
            // UPDATE correspondiente.
            carritoRepository.save(itemExistente);
            System.out.println("Servicio Carrito: Cantidad actualizada para el producto '" + producto.getNombre()
                    + "' para el usuario '" + emailUsuario + "'.");

        } else {
            // --- CASO B: El producto no está en el carrito ---
            // Creamos una nueva instancia de la entidad Carrito.
            Carrito nuevoItem = new Carrito(usuario, producto, cantidad);
            // Guardamos la nueva entidad. JPA se encargará de generar la consulta INSERT
            // correspondiente.
            carritoRepository.save(nuevoItem);
            System.out.println("Servicio Carrito: Nuevo producto '" + producto.getNombre()
                    + "' añadido para el usuario '" + emailUsuario + "'.");
        }
    }

    @Transactional
    public void eliminarProducto(String emailUsuario, Long productoId) {
        // Obtenemos las entidades principales
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        ProductoDigital producto = productoDigitalRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException(
                        "Producto no encontrado en la base de datos para eliminar del carrito"));

        // Buscamos la entrada del carrito para este usuario y producto
        Optional<Carrito> itemExistenteOpt = carritoRepository.findByUsuarioAndProductoDigital(usuario, producto);

        if (itemExistenteOpt.isPresent()) {
            // Si existe, lo eliminamos.
            carritoRepository.delete(itemExistenteOpt.get());
            System.out.println("CARRITO: Eliminado producto ID " + productoId + " para el usuario " + emailUsuario);
        } else {
            // Opcional: Podrías lanzar un warning o simplemente no hacer nada si se intenta
            // borrar algo que no existe.
            System.out.println("CARRITO: Se intentó eliminar un producto que no estaba en el carrito.");
        }
    }

    /**
     * Actualiza la cantidad de un producto específico en el carrito de un usuario.
     * Si la nueva cantidad es 0 o menor, el producto se elimina del carrito.
     *
     * @param emailUsuario  El email del usuario.
     * @param productoId    El ID del producto a actualizar.
     * @param nuevaCantidad La nueva cantidad deseada.
     */
    @Transactional
    public void actualizarCantidad(String emailUsuario, Long productoId, int nuevaCantidad) {
        if (nuevaCantidad <= 0) {
            // Si la cantidad es 0 o negativa, eliminamos el producto.
            eliminarProducto(emailUsuario, productoId);
            return;
        }

        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        ProductoDigital producto = productoDigitalRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Carrito item = carritoRepository.findByUsuarioAndProductoDigital(usuario, producto)
                .orElseThrow(() -> new RuntimeException("El item a actualizar no se encuentra en el carrito."));

        // Actualizamos la cantidad y guardamos.
        item.setCantidad(nuevaCantidad);
        carritoRepository.save(item);
        System.out.println("CARRITO: Actualizada cantidad a " + nuevaCantidad + " para producto ID " + productoId);
    }

    @Transactional
    public void limpiarCarrito(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Obtiene todos los items del carrito para ese usuario
        List<Carrito> items = carritoRepository.findByUsuario(usuario);

        // Los elimina todos
        if (!items.isEmpty()) {
            carritoRepository.deleteAll(items);
        }
    }
}
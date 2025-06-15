package com.example.ggmarket.controller;

import com.example.ggmarket.dto.CarritoUpdateDTO;
import com.example.ggmarket.service.CarritoService;
import com.example.ggmarket.service.CarritoViewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestionar las operaciones del carrito de compras.
 * Expone endpoints consumidos por el frontend de forma asíncrona.
 */
@RestController
@RequestMapping("/api/carrito")
public class CarritoApiController {

    private static final Logger log = LoggerFactory.getLogger(CarritoApiController.class);

    private final CarritoService carritoService;
    private final CarritoViewService carritoViewService;

    /**
     * Constructor que inyecta los servicios necesarios.
     *
     * @param carritoService servicio de lógica del carrito
     * @param carritoViewService servicio para visualización del contenido del carrito
     */
    public CarritoApiController(CarritoService carritoService, CarritoViewService carritoViewService) {
        this.carritoService = carritoService;
        this.carritoViewService = carritoViewService;
    }

    /**
     * Agrega un producto digital al carrito del usuario autenticado.
     *
     * @param payload JSON con el ID del producto a agregar
     * @param userDetails detalles del usuario autenticado
     * @return respuesta con éxito o error y nuevo total de ítems
     */
    @PostMapping("/agregar")
    public ResponseEntity<?> agregarAlCarrito(@RequestBody Map<String, Object> payload,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        log.info("API: Petición recibida en /api/carrito/agregar con payload: {}", payload);
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Debes iniciar sesión para realizar esta acción."));
        }
        try {
            Long productoId = Long.parseLong(payload.get("productoId").toString());
            carritoService.agregarProductoDigital(userDetails.getUsername(), productoId, 1);
            int nuevoTotalItems = carritoViewService.contarItemsUnicos(userDetails.getUsername());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Producto añadido al carrito.",
                    "nuevoTotalItems", nuevoTotalItems
            ));
        } catch (Exception e) {
            return manejarError(e, "agregar producto al carrito");
        }
    }

    /**
     * Obtiene el contenido actual del carrito del usuario autenticado.
     *
     * @param userDetails detalles del usuario autenticado
     * @return contenido del carrito o vacío si el usuario no está autenticado
     */
    @GetMapping("/contenido")
    public ResponseEntity<?> getContenidoCarrito(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.ok(Map.of("items", List.of(), "total", 0.0));
        }
        Map<String, Object> contenido = carritoViewService.getContenidoCarritoDTO(userDetails.getUsername());
        return ResponseEntity.ok(contenido);
    }

    /**
     * Elimina un producto del carrito del usuario autenticado.
     *
     * @param dto objeto con el ID del producto a eliminar
     * @param userDetails detalles del usuario autenticado
     * @return respuesta con el estado actualizado del carrito
     */
    @PostMapping("/eliminar")
    public ResponseEntity<?> eliminarItem(@RequestBody CarritoUpdateDTO dto,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        log.info("API: Petición recibida en /eliminar con DTO: {}", dto);
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Debes iniciar sesión para realizar esta acción."));
        }
        try {
            if (dto.getProductId() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Falta el ID del producto."));
            }
            carritoService.eliminarProducto(userDetails.getUsername(), dto.getProductId());
            return responderConEstadoActualizado(userDetails.getUsername(), "Producto eliminado.");
        } catch (Exception e) {
            return manejarError(e, "eliminar el producto");
        }
    }

    /**
     * Actualiza la cantidad de un producto en el carrito del usuario autenticado.
     *
     * @param dto objeto con el ID del producto y nueva cantidad
     * @param userDetails detalles del usuario autenticado
     * @return respuesta con el estado actualizado del carrito
     */
    @PostMapping("/actualizar-cantidad")
    public ResponseEntity<?> actualizarCantidad(@RequestBody CarritoUpdateDTO dto,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        log.info("API: Petición recibida en /actualizar-cantidad con DTO: {}", dto);
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Debes iniciar sesión para realizar esta acción."));
        }
        try {
            if (dto.getProductId() == null || dto.getNuevaCantidad() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Faltan datos en la petición."));
            }
            carritoService.actualizarCantidad(userDetails.getUsername(), dto.getProductId(), dto.getNuevaCantidad());
            return responderConEstadoActualizado(userDetails.getUsername(), "Cantidad actualizada.");
        } catch (Exception e) {
            return manejarError(e, "actualizar la cantidad");
        }
    }

    /**
     * Construye una respuesta HTTP 200 con el estado actualizado del carrito.
     *
     * @param username email del usuario
     * @param message mensaje a incluir en la respuesta
     * @return ResponseEntity con el contenido actualizado del carrito
     */
    private ResponseEntity<Map<String, Object>> responderConEstadoActualizado(String username, String message) {
        Map<String, Object> contenidoCarrito = carritoViewService.getContenidoCarritoDTO(username);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("nuevoTotal", contenidoCarrito.get("total"));
        response.put("nuevoTotalItems", ((List<?>) contenidoCarrito.get("items")).size());
        return ResponseEntity.ok(response);
    }

    /**
     * Maneja errores lanzados durante las operaciones de carrito.
     *
     * @param e excepción capturada
     * @param accion descripción de la acción que falló
     * @return ResponseEntity con el mensaje de error
     */
    private ResponseEntity<Map<String, Object>> manejarError(Exception e, String accion) {
        log.error("Error al intentar " + accion + ": {}", e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("success", false, "message", "Error al " + accion + "."));
    }
}

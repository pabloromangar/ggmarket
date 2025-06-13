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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador API REST para todas las operaciones relacionadas con el carrito
 * de compras.
 * Maneja peticiones asíncronas desde el frontend (JavaScript).
 */
@RestController
@RequestMapping("/api/carrito")
public class CarritoApiController {

    // Logger para registrar información y errores en la consola del servidor.
    private static final Logger log = LoggerFactory.getLogger(CarritoApiController.class);

    private final CarritoService carritoService;
    private final CarritoViewService carritoViewService;

    /**
     * Inyección de dependencias por constructor (práctica recomendada).
     */
    public CarritoApiController(CarritoService carritoService, CarritoViewService carritoViewService) {
        this.carritoService = carritoService;
        this.carritoViewService = carritoViewService;
    }

    /**
     * Endpoint para AÑADIR un producto al carrito.
     * Se activa cuando el usuario pulsa "Añadir al Carrito" en la tienda.
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
            int cantidad = 1; // Siempre se añade de uno en uno desde la tienda
            carritoService.agregarProductoDigital(userDetails.getUsername(), productoId, cantidad);

            // Devolvemos el nuevo total de ítems para actualizar el contador del header.
            int nuevoTotalItems = carritoViewService.contarItemsUnicos(userDetails.getUsername());
            return ResponseEntity.ok(Map.of("success", true, "message", "Producto añadido al carrito.",
                    "nuevoTotalItems", nuevoTotalItems));

        } catch (Exception e) {
            return manejarError(e, "agregar producto al carrito");
        }
    }

    /**
     * Endpoint para OBTENER el contenido completo del carrito del usuario.
     * Se usa para poblar el offcanvas del carrito.
     */
    @GetMapping("/contenido")
    public ResponseEntity<?> getContenidoCarrito(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            // Si no hay usuario, se devuelve un carrito vacío.
            return ResponseEntity.ok(Map.of("items", List.of(), "total", 0.0));
        }
        Map<String, Object> contenido = carritoViewService.getContenidoCarritoDTO(userDetails.getUsername());
        return ResponseEntity.ok(contenido);
    }

    // ===== ENDPOINT EXCLUSIVO PARA ELIMINAR =====
    @PostMapping("/eliminar")
    public ResponseEntity<?> eliminarItem(@RequestBody CarritoUpdateDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("API: Petición recibida en /eliminar con DTO: {}", dto);
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Debes iniciar sesión para realizar esta acción."));
        }

        try {
            // Validamos que el ID venga en el DTO
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

    // ===== ENDPOINT EXCLUSIVO PARA ACTUALIZAR CANTIDAD =====
    @PostMapping("/actualizar-cantidad")
    public ResponseEntity<?> actualizarCantidad(@RequestBody CarritoUpdateDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("API: Petición recibida en /actualizar-cantidad con DTO: {}", dto);
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Debes iniciar sesión para realizar esta acción."));
        }

        try {
            // Validamos que ambos campos vengan en el DTO
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

    // ...

    // ===================================================================
    // MÉTODOS PRIVADOS DE AYUDA (para no repetir código)
    // ===================================================================

    /**
     * Construye una respuesta HTTP 200 OK con el estado actualizado del carrito.
     * 
     * @param username El email del usuario.
     * @param message  El mensaje de éxito a incluir en la respuesta.
     * @return Un ResponseEntity con los datos actualizados del carrito.
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
     * Centraliza el manejo de errores para devolver una respuesta HTTP 400 Bad
     * Request consistente.
     * 
     * @param e      La excepción capturada.
     * @param accion La descripción de la acción que falló (ej. "eliminar el
     *               producto").
     * @return Un ResponseEntity con el mensaje de error.
     */
    private ResponseEntity<Map<String, Object>> manejarError(Exception e, String accion) {
        log.error("Error al intentar " + accion + ": {}", e.getMessage());
        e.printStackTrace(); // Imprime la traza completa para una depuración detallada.
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("success", false, "message", "Error al " + accion + "."));
    }
}
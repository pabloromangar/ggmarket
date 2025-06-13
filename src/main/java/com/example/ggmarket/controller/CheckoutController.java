package com.example.ggmarket.controller; // Asegúrate de que el package es el correcto

import com.example.ggmarket.model.Pedido;
import com.example.ggmarket.service.CarritoViewService;
import com.example.ggmarket.service.PedidoService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.Map;

@Controller
@RequestMapping("/checkout") // Agrupamos las rutas relacionadas con el checkout
public class CheckoutController {

    private final CarritoViewService carritoViewService;
    private final PedidoService pedidoService; // Asegúrate de que PedidoService está inyectado

    public CheckoutController(CarritoViewService carritoViewService, PedidoService pedidoService) {
        this.carritoViewService = carritoViewService;
        this.pedidoService = pedidoService;
    }

    /**
     * Muestra la página de checkout con el resumen del pedido y el formulario.
     */
    @GetMapping
    public String viewCheckoutPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Map<String, Object> contenidoCarrito = carritoViewService.getContenidoCarritoDTO(userDetails.getUsername());

        // Medida de seguridad: si el carrito está vacío, no se puede hacer checkout.
        if ((int) contenidoCarrito.get("totalItems") == 0) {
            return "redirect:/carrito";
        }

        model.addAttribute("cartItems", contenidoCarrito.getOrDefault("items", Collections.emptyList()));
        model.addAttribute("cartTotal", contenidoCarrito.getOrDefault("total", 0.0));
        model.addAttribute("pageTitle", "Finalizar Compra");

        return "clientes/checkout"; // Renderiza la plantilla checkout.html
    }

    /**
     * Procesa el formulario de checkout. Simula el pago y limpia el carrito.
     */
        @PostMapping("/procesar")
    public String processCheckout(RedirectAttributes redirectAttributes, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        try {
            // === ESTA ES LA LÍNEA CLAVE ===
            Pedido nuevoPedido = pedidoService.crearPedidoDesdeCarrito(userDetails.getUsername());
            // =============================
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "¡Gracias! Tu pedido #" + nuevoPedido.getId() + " ha sido creado con éxito.");

            return "redirect:/checkout/pedido-confirmado";

        } catch (IllegalStateException e) {
            // Esto ocurre si el carrito está vacío, por seguridad.
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/carrito";
       } catch (Exception e) {
    // ========= AÑADIR ESTAS LÍNEAS =========
    System.out.println("ERROR AL PROCESAR EL CHECKOUT: " + e); // Imprime el error completo en la consola
    // =======================================
    
    redirectAttributes.addFlashAttribute("errorMessage", "Hubo un error al procesar tu pedido. Revisa la consola del servidor.");
    return "redirect:/checkout";
}
    }

    @GetMapping("/pedido-confirmado")
    public String viewConfirmationPage(Model model) {
        // El mensaje de éxito se recibe desde el redirect
        // y Thymeleaf lo mostrará si existe.
        model.addAttribute("pageTitle", "Pedido Confirmado");
        return "clientes/pedido-confirmado";
    }
}
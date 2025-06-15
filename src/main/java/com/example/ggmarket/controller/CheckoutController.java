package com.example.ggmarket.controller;

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

/**
 * Controlador que maneja el proceso de checkout y creación de pedidos.
 */
@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final CarritoViewService carritoViewService;
    private final PedidoService pedidoService;

    /**
     * Constructor que inyecta los servicios necesarios para el checkout.
     *
     * @param carritoViewService servicio para obtener el contenido del carrito
     * @param pedidoService      servicio para crear pedidos
     */
    public CheckoutController(CarritoViewService carritoViewService, PedidoService pedidoService) {
        this.carritoViewService = carritoViewService;
        this.pedidoService = pedidoService;
    }

    /**
     * Muestra la página de checkout con el resumen del carrito y formulario de pago.
     *
     * @param model        modelo para pasar datos a la vista
     * @param userDetails  usuario autenticado
     * @return vista de la página de checkout
     */
    @GetMapping
    public String viewCheckoutPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Map<String, Object> contenidoCarrito = carritoViewService.getContenidoCarritoDTO(userDetails.getUsername());

        if ((int) contenidoCarrito.get("totalItems") == 0) {
            return "redirect:/carrito";
        }

        model.addAttribute("cartItems", contenidoCarrito.getOrDefault("items", Collections.emptyList()));
        model.addAttribute("cartTotal", contenidoCarrito.getOrDefault("total", 0.0));
        model.addAttribute("pageTitle", "Finalizar Compra");

        return "clientes/checkout";
    }

    /**
     * Procesa la compra simulando el pago y generando un nuevo pedido desde el carrito.
     *
     * @param redirectAttributes atributos para mostrar mensajes flash
     * @param userDetails        usuario autenticado
     * @return redirección a página de confirmación o de error
     */
    @PostMapping("/procesar")
    public String processCheckout(RedirectAttributes redirectAttributes,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        try {
            Pedido nuevoPedido = pedidoService.crearPedidoDesdeCarrito(userDetails.getUsername());

            redirectAttributes.addFlashAttribute("successMessage",
                    "¡Gracias! Tu pedido #" + nuevoPedido.getId() + " ha sido creado con éxito.");

            return "redirect:/checkout/pedido-confirmado";

        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/carrito";

        } catch (Exception e) {
            System.out.println("ERROR AL PROCESAR EL CHECKOUT: " + e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Hubo un error al procesar tu pedido. Revisa la consola del servidor.");
            return "redirect:/checkout";
        }
    }

    /**
     * Muestra la página de confirmación después de un pedido exitoso.
     *
     * @param model modelo para pasar atributos a la vista
     * @return vista de confirmación de pedido
     */
    @GetMapping("/pedido-confirmado")
    public String viewConfirmationPage(Model model) {
        model.addAttribute("pageTitle", "Pedido Confirmado");
        return "clientes/pedido-confirmado";
    }
}

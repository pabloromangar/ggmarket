package com.example.ggmarket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.ggmarket.model.Pedido;
import com.example.ggmarket.model.ProductoFisico;
import com.example.ggmarket.repository.ProductoFisicoRepository;
import com.example.ggmarket.service.PedidoService;

@Controller
public class PedidoFisicoController {

    @Autowired
    private ProductoFisicoRepository productoFisicoRepository;

    @Autowired
    private PedidoService pedidoService;

    // Muestra la página de confirmación ANTES de comprar
    @GetMapping("/comprar/fisico/{id}")
    public String showPhysicalCheckout(@PathVariable("id") Long productoId, Model model) {
        ProductoFisico producto = productoFisicoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        model.addAttribute("producto", producto);
        model.addAttribute("pageTitle", "Confirmar Compra");
        return "clientes/checkout-fisico";
    }

    @PostMapping("/comprar/fisico/procesar")
    public String processPhysicalOrder(@RequestParam("productoId") Long productoId, 
                                       @AuthenticationPrincipal UserDetails userDetails, 
                                       RedirectAttributes redirectAttributes) {
        
        // Comprobación de seguridad básica
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        try {
            // === ESTA DEBE SER LA ÚNICA LLAMADA A LA LÓGICA DE NEGOCIO ===
            // Llamamos al servicio para que se encargue de TODA la operación.
            Pedido nuevoPedido = pedidoService.crearPedidoParaProductoFisico(productoId, userDetails.getUsername());
            
            // Si todo va bien, preparamos el mensaje de éxito.
            redirectAttributes.addFlashAttribute("successMessage", 
                "¡Pedido #" + nuevoPedido.getId() + " realizado con éxito! El vendedor ha sido notificado.");
            
            // Y redirigimos al historial de pedidos.
            return "redirect:/mis-pedidos";

        } catch (IllegalStateException | AccessDeniedException e) {
            // Capturamos errores esperados (ej. producto ya vendido)
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/tienda/marketplace"; // Devolvemos al marketplace con un error
        } catch (Exception e) {
            // Capturamos cualquier otro error inesperado
            redirectAttributes.addFlashAttribute("errorMessage", "Ha ocurrido un error inesperado al procesar tu pedido.");
            return "redirect:/tienda/marketplace";
        }
    }
}
package com.example.ggmarket.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

    // Procesa la compra
    @PostMapping("/comprar/fisico/procesar")
    public String processPhysicalOrder(@RequestParam("productoId") Long productoId,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        Pedido nuevoPedido = pedidoService.crearPedidoParaProductoFisico(productoId, userDetails.getUsername());
        redirectAttributes.addFlashAttribute("successMessage",
                "¡Pedido #" + nuevoPedido.getId()
                        + " realizado! El vendedor ha sido notificado para que prepare el envío.");
        ProductoFisico producto = productoFisicoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        productoFisicoRepository.delete(producto);

        return "redirect:/mis-pedidos";
    }
}
package com.example.ggmarket.controller;

import com.example.ggmarket.model.Pedido;
import com.example.ggmarket.service.PedidoService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/mis-pedidos")
    public String viewUserOrders(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        // Llamamos al servicio para obtener la lista de pedidos
        List<Pedido> pedidos = pedidoService.findPedidosByUsuarioEmail(userDetails.getUsername());
        
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("pageTitle", "Mis Pedidos");

        return "clientes/mis-pedidos"; // Renderiza la plantilla mis-pedidos.html
    }
}
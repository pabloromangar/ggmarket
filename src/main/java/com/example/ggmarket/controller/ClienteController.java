package com.example.ggmarket.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ggmarket.model.ProductoDigital;
import com.example.ggmarket.service.CarritoViewService;
import com.example.ggmarket.service.ProductoDigitalService;

@Controller
public class ClienteController {

    @Autowired
    private ProductoDigitalService productoDigitalService;

    @Autowired
    private CarritoViewService carritoViewService;
    
    @GetMapping("/")
    public String index() {
        return "clientes/index"; // Redirige a la página de inicio
    }
     @GetMapping("/tienda")
    public String indice() {
        return "clientes/index"; // Redirige a la página de inicio
    }
    @GetMapping("/tienda/productosDigitales")
    public String listarProductos(Model model,
            @RequestParam(defaultValue = "0") int page) {
        Page<ProductoDigital> productos = productoDigitalService.obtenerProductos(PageRequest.of(page, 10));
        model.addAttribute("productos", productos);
        return "clientes/lista";
    }

     @GetMapping("/tienda/productoDigital/{id}")
    public String verProducto(@PathVariable Long id, Model model) {
        ProductoDigital producto = productoDigitalService.findById(id);
        model.addAttribute("producto", producto);
        return "clientes/productoDigital";
    }

     @GetMapping("/carrito")
    public String viewCartPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            // Si el usuario no está logueado, redirigir al login
            return "redirect:/login";
        }

        // Usamos el servicio que ya tienes para obtener el contenido y el total
        Map<String, Object> contenidoCarrito = carritoViewService.getContenidoCarritoDTO(userDetails.getUsername());

        model.addAttribute("cartItems", contenidoCarrito.getOrDefault("items", Collections.emptyList()));
        model.addAttribute("cartTotal", contenidoCarrito.getOrDefault("total", 0.0));
        model.addAttribute("pageTitle", "Tu Carrito de Compras");

        return "clientes/carrito"; // Renderiza la plantilla 'carrito.html'
    }

}

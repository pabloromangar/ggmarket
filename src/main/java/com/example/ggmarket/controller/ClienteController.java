package com.example.ggmarket.controller;

import java.util.Collections;
import java.util.List;
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
import com.example.ggmarket.model.ProductoFisico;
import com.example.ggmarket.repository.ProductoDigitalRepository;
import com.example.ggmarket.repository.ProductoFisicoRepository;
import com.example.ggmarket.service.CarritoViewService;
import com.example.ggmarket.service.ProductoDigitalService;

@Controller
public class ClienteController {

    @Autowired
    private ProductoFisicoRepository productoFisicoRepository;

    @Autowired
    private ProductoDigitalService productoDigitalService;

    @Autowired
    private CarritoViewService carritoViewService;

    @Autowired
    private ProductoDigitalRepository productoDigitalRepository;

    @GetMapping("/")
    public String index() {
        return "redirect:/tienda"; // Redirige a la página de inicio de la tienda
    }

    @GetMapping("/tienda")
    public String indice(Model model) {
        List<ProductoDigital> juegosBaratos = productoDigitalRepository.findByOrderByPrecioAsc(PageRequest.of(0, 4));

        // 2. Obtenemos las 4 primeras tarjetas de saldo (asumiendo que tienen tipo 'TARJETA')
        List<ProductoDigital> tarjetas = productoDigitalRepository.findByTipoOrderByNombreAsc("TARJETA", PageRequest.of(0, 4));
        
        // 3. Obtenemos los 4 primeros códigos de Windows (asumiendo tipo 'SOFTWARE')
        List<ProductoDigital> software = productoDigitalRepository.findByTipoOrderByNombreAsc("SOFTWARE", PageRequest.of(0, 4));

        // Añadimos las listas al modelo para que Thymeleaf pueda usarlas
        model.addAttribute("juegosBaratos", juegosBaratos);
        model.addAttribute("tarjetas", tarjetas);
        model.addAttribute("software", software);
        model.addAttribute("pageTitle", "Tu Tienda de Claves Digitales");

        return "clientes/index"; // Renderiza la plantilla index.html
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

    @GetMapping("/tienda/marketplace")
    public String showMarketplace(Model model, @RequestParam(defaultValue = "0") int page) {
        // Obtenemos los productos físicos de forma paginada (ej. 12 por página)
        Page<ProductoFisico> productos = productoFisicoRepository.findAll(PageRequest.of(page, 12));

        model.addAttribute("productosFisicos", productos);
        model.addAttribute("pageTitle", "Marketplace de Productos Físicos");

        return "clientes/marketplace"; // Renderiza la plantilla marketplace.html
    }
}

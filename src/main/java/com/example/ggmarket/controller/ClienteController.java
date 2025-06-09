package com.example.ggmarket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ggmarket.model.ProductoDigital;
import com.example.ggmarket.service.ProductoDigitalService;

@Controller
public class ClienteController {

    @Autowired
    private ProductoDigitalService productoDigitalService;

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

}

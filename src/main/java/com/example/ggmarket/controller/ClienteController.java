package com.example.ggmarket.controller;

import com.example.ggmarket.model.ProductoDigital;
import com.example.ggmarket.model.ProductoFisico;
import com.example.ggmarket.model.Usuario;
import com.example.ggmarket.repository.ProductoFisicoRepository;
import com.example.ggmarket.repository.UsuarioRepository;
import com.example.ggmarket.service.CarritoViewService;
import com.example.ggmarket.service.ProductoDigitalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // ¡Importante!
import org.springframework.data.web.PageableDefault; // ¡Importante!
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collections;
import java.util.Map;

@Controller
public class ClienteController {

    // --- Inyección de dependencias por constructor (Mejor Práctica) ---
    private final ProductoFisicoRepository productoFisicoRepository;
    private final ProductoDigitalService productoDigitalService;
    private final CarritoViewService carritoViewService;
    private final UsuarioRepository usuarioRepository;

    public ClienteController(ProductoFisicoRepository productoFisicoRepository,
                             ProductoDigitalService productoDigitalService,
                             CarritoViewService carritoViewService,
                             UsuarioRepository usuarioRepository) {
        this.productoFisicoRepository = productoFisicoRepository;
        this.productoDigitalService = productoDigitalService;
        this.carritoViewService = carritoViewService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/tienda";
    }

    @GetMapping("/tienda")
    public String indice(Model model) {
        // La lógica ahora está en el servicio, el controlador solo llama.
        model.addAttribute("juegosBaratos", productoDigitalService.findJuegosBaratos(4));
        model.addAttribute("tarjetas", productoDigitalService.findPorTipo("TARJETA", 4));
        model.addAttribute("software", productoDigitalService.findPorTipo("SOFTWARE", 4));
        model.addAttribute("pageTitle", "Tu Tienda de Claves Digitales");
        return "clientes/index";
    }

    @GetMapping("/tienda/productosDigitales")
    public String listarProductos(Model model,
                                  // --- MANEJO DE PAGEABLE MEJORADO ---
                                  // Spring crea el Pageable por nosotros.
                                  // Podemos configurar valores por defecto.
                                  @PageableDefault(size = 12, sort = "id") Pageable pageable) {
        Page<ProductoDigital> productos = productoDigitalService.obtenerProductos(pageable);
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
            return "redirect:/login";
        }
        Map<String, Object> contenidoCarrito = carritoViewService.getContenidoCarritoDTO(userDetails.getUsername());
        model.addAttribute("cartItems", contenidoCarrito.getOrDefault("items", Collections.emptyList()));
        model.addAttribute("cartTotal", contenidoCarrito.getOrDefault("total", 0.0));
        model.addAttribute("pageTitle", "Tu Carrito de Compras");
        return "clientes/carrito";
    }

    @GetMapping("/tienda/marketplace")
    public String showMarketplace(Model model,
                                  @PageableDefault(size = 12) Pageable pageable, // <-- PAGEABLE MEJORADO
                                  @AuthenticationPrincipal UserDetails userDetails) {
        Page<ProductoFisico> productos;
        if (userDetails != null) {
            Usuario usuarioActual = usuarioRepository.findByEmail(userDetails.getUsername()).orElse(null);
            if (usuarioActual != null) {
                productos = productoFisicoRepository.findByVendidoIsFalseAndVendedorNot(usuarioActual, pageable);
            } else {
                productos = productoFisicoRepository.findByVendidoIsFalse(pageable);
            }
        } else {
            productos = productoFisicoRepository.findByVendidoIsFalse(pageable);
        }
        model.addAttribute("productosFisicos", productos);
        model.addAttribute("pageTitle", "Marketplace de Productos Físicos");
        return "clientes/marketplace";
    }
}
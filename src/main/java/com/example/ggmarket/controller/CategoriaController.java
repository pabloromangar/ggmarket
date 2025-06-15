package com.example.ggmarket.controller;

import com.example.ggmarket.model.ProductoDigital;
import com.example.ggmarket.repository.ProductoDigitalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controlador que gestiona la visualización de productos digitales filtrados por tipo o plataforma.
 */
@Controller
public class CategoriaController {

    private final ProductoDigitalRepository productoDigitalRepository;

    /**
     * Constructor que inyecta el repositorio de productos digitales.
     *
     * @param productoDigitalRepository repositorio para acceder a los productos digitales
     */
    public CategoriaController(ProductoDigitalRepository productoDigitalRepository) {
        this.productoDigitalRepository = productoDigitalRepository;
    }

    /**
     * Muestra los productos filtrados por tipo (por ejemplo, "juego", "software").
     *
     * @param tipo     tipo de producto a filtrar (insensible a mayúsculas/minúsculas)
     * @param pageable objeto que define la paginación
     * @param model    modelo para pasar datos a la vista
     * @return nombre de la plantilla de vista
     */
    @GetMapping("/tienda/tipo/{tipo}")
    public String listarPorTipo(@PathVariable String tipo,
                                @PageableDefault(size = 12) Pageable pageable,
                                Model model) {

        Page<ProductoDigital> productos = productoDigitalRepository.findByTipoIgnoreCase(tipo, pageable);

        model.addAttribute("productos", productos);
        model.addAttribute("pageTitle", "Categoría: " + tipo);
        model.addAttribute("currentFilterType", "tipo");
        model.addAttribute("currentFilterValue", tipo);

        return "clientes/categoria-lista";
    }

    /**
     * Muestra los productos filtrados por plataforma (por ejemplo, "steam", "psn").
     *
     * @param plataforma nombre de la plataforma (insensible a mayúsculas/minúsculas)
     * @param pageable   objeto que define la paginación
     * @param model      modelo para pasar datos a la vista
     * @return nombre de la plantilla de vista
     */
    @GetMapping("/tienda/plataforma/{plataforma}")
    public String listarPorPlataforma(@PathVariable String plataforma,
                                      @PageableDefault(size = 12) Pageable pageable,
                                      Model model) {

        Page<ProductoDigital> productos = productoDigitalRepository.findByPlataformaIgnoreCase(plataforma, pageable);

        model.addAttribute("productos", productos);
        model.addAttribute("pageTitle", "Plataforma: " + plataforma);
        model.addAttribute("currentFilterType", "plataforma");
        model.addAttribute("currentFilterValue", plataforma);

        return "clientes/categoria-lista";
    }
}

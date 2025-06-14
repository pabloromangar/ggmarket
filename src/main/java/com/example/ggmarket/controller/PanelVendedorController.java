package com.example.ggmarket.controller;

import com.example.ggmarket.model.Pedido;
import com.example.ggmarket.model.ProductoFisico;
import com.example.ggmarket.service.PedidoService;
import com.example.ggmarket.service.ProductoFisicoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/panel-vendedor")
@PreAuthorize("hasRole('VENDEDOR')") // ¡Seguridad! Solo los vendedores pueden acceder a estas rutas.
public class PanelVendedorController {

    @Autowired
    private ProductoFisicoService productoFisicoService;

    @Autowired
    private PedidoService pedidoService;

    /**
     * Muestra la página principal del panel del vendedor con la lista de sus
     * productos.
     */
    @GetMapping
    public String viewPanel(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        List<ProductoFisico> misProductos = productoFisicoService
                .findProductosByVendedorEmail(userDetails.getUsername());
        model.addAttribute("productos", misProductos);
        model.addAttribute("pageTitle", "Panel de Vendedor");
        return "vendedor/panel"; // Renderiza la plantilla panel.html
    }

    /**
     * Muestra el formulario para crear un nuevo producto.
     */
    @GetMapping("/productos/nuevo")
    public String showNewProductForm(Model model) {
        model.addAttribute("productoFisico", new ProductoFisico());
        model.addAttribute("pageTitle", "Añadir Nuevo Producto");
        return "vendedor/formulario-producto"; // Renderiza el formulario
    }

    /**
     * Muestra el formulario para editar un producto existente.
     */
    @GetMapping("/productos/editar/{id}")
    public String showEditProductForm(@PathVariable("id") Long productoId, Model model,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Buscamos el producto asegurándonos de que pertenece al vendedor logueado
        ProductoFisico producto = productoFisicoService
                .findProductoByIdAndVendedorEmail(productoId, userDetails.getUsername())
                .orElseThrow(
                        () -> new AccessDeniedException("Producto no encontrado o no tienes permiso para editarlo."));

        model.addAttribute("productoFisico", producto);
        model.addAttribute("pageTitle", "Editar Producto");

        // ¡Reutilizamos la misma vista del formulario!
        return "vendedor/formulario-producto";
    }

    /**
     * Procesa tanto la creación de un nuevo producto como la actualización de uno
     * existente.
     */
    @PostMapping("/productos/guardar")
    public String saveOrUpdateProduct(@ModelAttribute ProductoFisico producto,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        try {
            // Si el producto tiene un ID, es una actualización. Si no, es una creación.
            boolean isNew = producto.getId() == null;

            if (isNew) {
                productoFisicoService.save(producto, userDetails.getUsername());
                redirectAttributes.addFlashAttribute("successMessage", "Producto creado con éxito.");
            } else {
                // Para la actualización, también verificamos la propiedad
                ProductoFisico existingProduct = productoFisicoService
                        .findProductoByIdAndVendedorEmail(producto.getId(), userDetails.getUsername())
                        .orElseThrow(
                                () -> new AccessDeniedException("Intento de modificación de producto no autorizado."));

                // Actualizamos los campos y guardamos
                existingProduct.setNombre(producto.getNombre());
                existingProduct.setDescripcion(producto.getDescripcion());
                existingProduct.setPrecio(producto.getPrecio());
                existingProduct.setImagenUrl(producto.getImagenUrl());
                productoFisicoService.update(existingProduct);
                redirectAttributes.addFlashAttribute("successMessage", "Producto actualizado con éxito.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar el producto: " + e.getMessage());
        }
        return "redirect:/panel-vendedor";
    }

    @PostMapping("/productos/eliminar/{id}")
    public String deleteProduct(@PathVariable("id") Long productoId,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        try {
            productoFisicoService.deleteProducto(productoId, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Producto eliminado con éxito.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el producto: " + e.getMessage());
        }
        return "redirect:/panel-vendedor";
    }
    @GetMapping("/ventas")
public String viewSales(Model model, @AuthenticationPrincipal UserDetails userDetails) {
    List<Pedido> ventas = pedidoService.findPedidosParaVendedor(userDetails.getUsername());
    model.addAttribute("ventas", ventas);
    model.addAttribute("pageTitle", "Mis Ventas");
    return "vendedor/ventas";
}

// Procesa la confirmación del envío
@PostMapping("/ventas/confirmar/{id}")
public String confirmShipment(@PathVariable("id") Long pedidoId, 
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
    try {
        pedidoService.confirmarEnvio(pedidoId, userDetails.getUsername());
        redirectAttributes.addFlashAttribute("successMessage", "Envío del pedido #" + pedidoId + " confirmado.");
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("errorMessage", "Error al confirmar el envío.");
    }
    return "redirect:/panel-vendedor/ventas";
}
}
package com.example.ggmarket.controller;

import com.example.ggmarket.model.Pedido;
import com.example.ggmarket.model.ProductoFisico;
import com.example.ggmarket.service.CloudinaryService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/panel-vendedor")
@PreAuthorize("hasRole('VENDEDOR')") // ¡Seguridad! Solo los vendedores pueden acceder a estas rutas.
public class PanelVendedorController {

    @Autowired
    private ProductoFisicoService productoFisicoService;

    @Autowired
    private CloudinaryService cloudinaryService; // Asegúrate de tener este servicio para manejar imágenes
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
    public String saveOrUpdateProduct(
            @ModelAttribute("productoFisico") ProductoFisico productoForm,
            @RequestParam("imagenFile") MultipartFile imagenFile, // <-- Nuevo parámetro para el archivo
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        
        try {
            // --- LÓGICA DE SUBIDA DE IMAGEN ---
            // Comprobamos si el usuario ha subido un archivo nuevo.
            if (!imagenFile.isEmpty()) {
                // Si hay un archivo, lo subimos a Cloudinary.
                String imageUrl = cloudinaryService.uploadFile(imagenFile);
                // Guardamos la URL segura devuelta por Cloudinary en nuestro objeto.
                productoForm.setImagenUrl(imageUrl);
            }
            // Si no se sube un archivo nuevo en una edición, se mantendrá la URL existente.
            // --- FIN DE LA LÓGICA DE IMAGEN ---


            // La lógica de guardar/actualizar el producto sigue igual.
            boolean isNew = productoForm.getId() == null;

            if (isNew) {
                productoFisicoService.save(productoForm, userDetails.getUsername());
                redirectAttributes.addFlashAttribute("successMessage", "Producto creado con éxito.");
            } else {
                productoFisicoService.update(productoForm, userDetails.getUsername()); // Pasamos los dos parámetros
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
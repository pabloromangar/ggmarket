package com.example.ggmarket.controller;

import com.example.ggmarket.service.CarritoViewService; // Un nuevo servicio para la vista
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Esta clase añade atributos al modelo de todas las vistas renderizadas por Thymeleaf.
 * Es ideal para datos que se necesitan en todas las páginas, como el número de ítems en el carrito.
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private CarritoViewService carritoViewService; // Usaremos un servicio específico para esto

    /**
     * Este método se ejecuta antes de cualquier método de un @Controller.
     * Añade el número de ítems en el carrito al modelo para que esté disponible en el header.
     *
     * @param userDetails Los detalles del usuario autenticado. Será null si el usuario es anónimo.
     * @return El número de ítems distintos en el carrito del usuario.
     */
    @ModelAttribute("itemsEnCarrito")
    public int getItemsEnCarrito(@AuthenticationPrincipal UserDetails userDetails) {
        // Si el usuario no está logueado, su carrito tiene 0 ítems.
        if (userDetails == null) {
            return 0;
        }
        // Si está logueado, le pedimos al servicio que cuente los ítems.
        return carritoViewService.contarItemsUnicos(userDetails.getUsername());
    }
}
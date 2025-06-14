package com.example.ggmarket.controller;

import com.example.ggmarket.model.Usuario;
import com.example.ggmarket.repository.UsuarioRepository;
import com.example.ggmarket.service.impl.UsuarioServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PerfilController {

    @Autowired
    private UsuarioServiceImpl usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ... otros mappings que puedas tener para el perfil ...

    /**
     * Endpoint que procesa la solicitud del usuario para convertirse en vendedor.
     */
    @PostMapping("/perfil/convertirse-vendedor")
    public String procesarConversionVendedor(
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        try {
            String userEmail = userDetails.getUsername();

            // 1. Convertimos al usuario (la base de datos se actualiza)
            usuarioService.convertirUsuarioAVendedor(userEmail);

            // 2. === ¡EL PASO CLAVE! ===
            // Refrescamos la sesión del usuario con su nuevo rol.
            usuarioService.updateUserAuthentication(userEmail);

            redirectAttributes.addFlashAttribute("successMessage",
                    "¡Felicidades! Tu cuenta ha sido actualizada. Ya puedes empezar a vender.");
            return "redirect:/panel-vendedor";

        } catch (Exception e) {
            // ... (tu manejo de errores)
            return "redirect:/perfil"; // O la página donde estaba el botón
        }
    }

    @GetMapping("/perfil")
    public String viewProfilePage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        // Buscamos al usuario en la base de datos para obtener todos sus datos
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        model.addAttribute("usuario", usuario);
        model.addAttribute("pageTitle", "Mi Perfil");

        return "clientes/perfil"; // Renderiza la plantilla perfil.html
    }
}
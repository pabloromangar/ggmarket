package com.example.ggmarket.controller;

import com.example.ggmarket.dto.UsuarioRegistroDTO; // <-- Usamos el DTO
import com.example.ggmarket.service.UsuarioService; // <-- Inyectamos la interfaz, no la implementación

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.example.ggmarket.repository.UsuarioRepository; // Para la comprobación de email

@Controller
public class AuthController {

    // --- INYECCIÓN DE DEPENDENCIAS MEJORADA ---
    // Se inyecta la interfaz del servicio, no la clase concreta (buena práctica).
    private final UsuarioService usuarioServicio;
    // Inyectamos el repositorio solo para la comprobación (alternativa en el
    // servicio)
    private final UsuarioRepository usuarioRepository;

    public AuthController(UsuarioService usuarioServicio, UsuarioRepository usuarioRepository) {
        this.usuarioServicio = usuarioServicio;
        this.usuarioRepository = usuarioRepository;
    }

    // --- MOSTRAR FORMULARIO DE LOGIN ---
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    // --- MOSTRAR FORMULARIO DE REGISTRO ---
    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        // Le pasamos un objeto DTO vacío a la vista, no la entidad.
        model.addAttribute("usuarioDTO", new UsuarioRegistroDTO());
        return "registro";
    }

    // --- PROCESAR REGISTRO ---
    @PostMapping("/registro")
    public String registrarUsuario(@Valid @ModelAttribute("usuarioDTO") UsuarioRegistroDTO registroDTO,
            BindingResult result,
            Model model) {

        // 1. Comprobar si el email ya existe
        if (usuarioRepository.existsByEmail(registroDTO.getEmail())) {
            // Añadimos un error específico al campo 'email'
            result.rejectValue("email", "error.usuario", "Este correo electrónico ya está registrado");
        }

        // 2. Comprobar si hay errores de validación (del @Valid o el que acabamos de
        // añadir)
        if (result.hasErrors()) {
            return "registro"; // Si hay errores, volvemos al formulario
        }

        // 3. Llamar al servicio para que haga todo el trabajo
        // El controlador ya no sabe nada de contraseñas, roles, etc.
        usuarioServicio.guardar(registroDTO);

        return "redirect:/login?exito";
    }
}
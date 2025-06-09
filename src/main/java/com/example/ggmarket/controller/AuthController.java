package com.example.ggmarket.controller;

import com.example.ggmarket.model.Rol;
import com.example.ggmarket.model.Usuario;
import com.example.ggmarket.service.impl.UsuarioServiceImpl;

import jakarta.validation.Valid;

import com.example.ggmarket.service.RolService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
public class AuthController {

    @Autowired
    private UsuarioServiceImpl usuarioService;

    @Autowired
    private RolService rolService; // Para asignar rol al usuario registrado

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Mostrar formulario de registro
    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro"; // nombre de la plantilla Thymeleaf registro.html
    }

    // Procesar registro
    @PostMapping("/registro")
    public String registrarUsuario(@Valid @ModelAttribute("usuario") Usuario usuario,
                                  BindingResult result,
                                  Model model) {
        if (result.hasErrors()) {
            return "registro";
        }

        if (usuarioService.findByEmail(usuario.getEmail()) != null) {
            model.addAttribute("errorEmail", "El email ya está registrado");
            return "registro";
        }

        // Encriptar la contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // Asignar rol CLIENTE (por ejemplo)
        Rol rolCliente = rolService.findByNombre("CLIENTE");
        usuario.setRol(rolCliente);

        usuarioService.save(usuario);

        return "redirect:/login?registroExitoso";
    }

    // Mostrar formulario de login
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login"; // nombre de la plantilla Thymeleaf login.html
    }
}

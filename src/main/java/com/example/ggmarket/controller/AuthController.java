package com.example.ggmarket.controller;

import com.example.ggmarket.dto.UsuarioRegistroDTO;
import com.example.ggmarket.service.UsuarioService;
import com.example.ggmarket.repository.UsuarioRepository;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador que gestiona la autenticación y registro de usuarios.
 */
@Controller
public class AuthController {

    private final UsuarioService usuarioServicio;
    private final UsuarioRepository usuarioRepository;

    public AuthController(UsuarioService usuarioServicio, UsuarioRepository usuarioRepository) {
        this.usuarioServicio = usuarioServicio;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Muestra el formulario de inicio de sesión.
     *
     * @return Nombre de la vista del login.
     */
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    /**
     * Muestra el formulario de registro de usuario.
     *
     * @param model Modelo para pasar datos a la vista.
     * @return Nombre de la vista del formulario de registro.
     */
    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuarioDTO", new UsuarioRegistroDTO());
        return "registro";
    }

    /**
     * Procesa los datos enviados desde el formulario de registro.
     *
     * @param registroDTO Datos del usuario a registrar.
     * @param result Resultado de la validación del formulario.
     * @param model Modelo para pasar datos a la vista en caso de error.
     * @return Redirección al login en caso de éxito o recarga del formulario si hay errores.
     */
    @PostMapping("/registro")
    public String registrarUsuario(@Valid @ModelAttribute("usuarioDTO") UsuarioRegistroDTO registroDTO,
                                   BindingResult result,
                                   Model model) {

        if (usuarioRepository.existsByEmail(registroDTO.getEmail())) {
            result.rejectValue("email", "error.usuario", "Este correo electrónico ya está registrado");
        }

        if (result.hasErrors()) {
            return "registro";
        }

        usuarioServicio.guardar(registroDTO);

        return "redirect:/login?exito";
    }
}

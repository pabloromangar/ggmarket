package com.example.ggmarket.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.ggmarket.model.Usuario;
import com.example.ggmarket.service.UsuarioService;

@Controller
public class UsuarioWebController {
    
    @Autowired
    UsuarioService usuarioService;

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.findAll();
        model.addAttribute("usuarios", usuarios);
        return "usuarios/lista"; // Va a buscar usuarios/lista.html en templates
    }
}

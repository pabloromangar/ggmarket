package com.example.ggmarket.service.impl;

import com.example.ggmarket.dto.UsuarioRegistroDTO;
import com.example.ggmarket.model.Rol;
import com.example.ggmarket.model.Usuario;
import com.example.ggmarket.repository.RolRepository;
import com.example.ggmarket.repository.UsuarioRepository;
import com.example.ggmarket.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario findById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    @Override
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario saveUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Object findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Override
    public Usuario guardar(UsuarioRegistroDTO registroDTO) {
        // 1. Busca el rol por defecto. Si no existe, lo crea.
        Rol rolCliente = rolRepository.findByNombre("CLIENTE");
        // 2. Crea la nueva entidad Usuario
        // Nota: Asegúrate de que tu entidad Usuario tenga un constructor que coincida.
        Usuario usuario = new Usuario(
                registroDTO.getNombre(),
                registroDTO.getEmail(),
                // 3. Encripta la contraseña
                passwordEncoder.encode(registroDTO.getPassword()),
                rolCliente);

        // 4. Guarda el usuario en la base de datos
        return usuarioRepository.save(usuario);
    }

}
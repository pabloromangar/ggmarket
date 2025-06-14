package com.example.ggmarket.service.impl;

import com.example.ggmarket.dto.UsuarioRegistroDTO;
import com.example.ggmarket.model.Rol;
import com.example.ggmarket.model.Usuario;
import com.example.ggmarket.repository.RolRepository;
import com.example.ggmarket.repository.UsuarioRepository;
import com.example.ggmarket.service.UsuarioService;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    @Autowired 
    private UsuarioDetailsServiceImpl userDetailsService;

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

    // En tu UsuarioService.java o donde tengas este método

    @Override
    public Usuario guardar(UsuarioRegistroDTO registroDTO) {
        // 1. Busca el rol por defecto y lo "desenvuelve" del Optional.
        // Si no lo encuentra, lanza una excepción y detiene el proceso.
        Rol rolCliente = rolRepository.findByNombre("CLIENTE")
                .orElseThrow(() -> new IllegalStateException(
                        "El rol 'CLIENTE' por defecto no se encuentra en la base de datos. Por favor, créalo."));

        // 2. Crea la nueva entidad Usuario
        Usuario usuario = new Usuario(
                registroDTO.getNombre(),
                registroDTO.getEmail().toLowerCase().trim(), // Buena práctica: normalizar el email
                passwordEncoder.encode(registroDTO.getPassword()),
                rolCliente // Ahora sí le pasas un objeto Rol, no un Optional
        );

        // 3. Guarda el usuario en la base de datos
        return usuarioRepository.save(usuario);
    }
    // En tu UsuarioService.java

    @Transactional
    public void convertirUsuarioAVendedor(String emailUsuario) {
        // 1. Busca el rol y lo "desenvuelve". Si no existe, lanza una excepción.
        Rol rolVendedor = rolRepository.findByNombre("VENDEDOR")
                .orElseThrow(() -> new IllegalStateException(
                        "El rol 'VENDEDOR' no ha sido encontrado en la base de datos."));

        // El 'if (rolVendedor == null)' ya no es necesario, porque orElseThrow() ya ha
        // hecho esa comprobación.

        // 2. Buscamos al usuario que queremos modificar.
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + emailUsuario));

        // 3. Actualizamos el rol del usuario.
        usuario.setRol(rolVendedor);

        // 4. Guardamos los cambios.
        usuarioRepository.save(usuario);
    }

    /**
     * MÉTODO NUEVO:
     * Actualiza la sesión del usuario actual con sus nuevos roles/permisos.
     * 
     * @param email El email del usuario cuya sesión se va a refrescar.
     */
    public void updateUserAuthentication(String email) {
        // 1. Recargamos los detalles del usuario desde la base de datos (ahora con el
        // nuevo rol)
        UserDetails updatedUserDetails = userDetailsService.loadUserByUsername(email);

        // 2. Creamos un nuevo objeto de autenticación con los permisos actualizados
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                updatedUserDetails,
                null, // No necesitamos la contraseña aquí
                updatedUserDetails.getAuthorities() // La lista de permisos ACTUALIZADA
        );

        // 3. Establecemos la nueva autenticación en el contexto de seguridad
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
}
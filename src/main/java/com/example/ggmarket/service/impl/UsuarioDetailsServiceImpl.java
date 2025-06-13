package com.example.ggmarket.service.impl;

import com.example.ggmarket.model.Usuario;
import com.example.ggmarket.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class UsuarioDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    // --- INICIO DE DEPURACIÓN ---
    System.out.println("======================================================");
    System.out.println("Auth Check: Intentando cargar usuario por email: " + email);
    // --- FIN DE DEPURACIÓN ---

    Usuario usuario = usuarioRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

    if (usuario == null) {
        // --- INICIO DE DEPURACIÓN ---
        System.out.println("Auth Check: FALLO - Usuario con email '" + email + "' no encontrado en la BBDD.");
        System.out.println("======================================================");
        // --- FIN DE DEPURACIÓN ---
        throw new UsernameNotFoundException("Usuario o contraseña inválidos.");
    }

    // --- INICIO DE DEPURACIÓN ---
    System.out.println("Auth Check: ÉXITO - Usuario encontrado: " + usuario.getEmail());
    System.out.println("Auth Check: Rol del usuario: " + usuario.getRol().getNombre());
    System.out.println("Auth Check: Password HASH de la BBDD: " + usuario.getPassword());
    System.out.println("======================================================");
    // --- FIN DE DEPURACIÓN ---

    GrantedAuthority authority = new SimpleGrantedAuthority(usuario.getRol().getNombre());
    return new User(usuario.getEmail(), usuario.getPassword(), Collections.singletonList(authority));
}
}
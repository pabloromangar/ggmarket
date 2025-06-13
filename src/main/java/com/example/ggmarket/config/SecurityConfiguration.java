package com.example.ggmarket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

   @Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(auth -> auth
            // --- RUTAS PÚBLICAS ---
            // Cualquiera puede acceder a estas URLs
            .requestMatchers(
                "/",                  // Página principal
                "/tienda/**",            // Página del catálogo
                "/producto/**",       // Detalle de cualquier producto (ej: /producto/123)
                "/registro",          // Página de registro
                "/login",             // Página de login
                "/css/**",            // Archivos CSS
                "/js/**",             // Archivos JavaScript
                "/img/**"             // Imágenes
            ).permitAll()

            // --- RUTAS DE ADMINISTRADOR ---
            // Solo usuarios con rol 'ADMIN' pueden acceder
            .requestMatchers("/admin/**").hasAuthority("ADMIN")

            // --- CUALQUIER OTRA RUTA ---
            // Todas las demás URLs requieren que el usuario esté autenticado
            .anyRequest().authenticated()
        )
        // La configuración de formLogin se encarga de la redirección automática
        .formLogin(form -> form
            .loginPage("/login")
            .loginProcessingUrl("/login")
            .usernameParameter("email")
            .defaultSuccessUrl("/", true)
            .failureUrl("/login?error=true")
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login?logout=true")
            .permitAll()
        );

    return http.build();
}
}
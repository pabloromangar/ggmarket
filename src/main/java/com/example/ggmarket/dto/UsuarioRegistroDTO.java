package com.example.ggmarket.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsuarioRegistroDTO {

    private String nombre;
    private String apellido; // Asumo que finalmente lo incluirás, como en el modelo que te sugerí. Si no, bórralo.
    private String email;
    private String password;

    // Puedes añadir constructores si los necesitas
    public UsuarioRegistroDTO(String nombre, String apellido, String email, String password) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
    }
}
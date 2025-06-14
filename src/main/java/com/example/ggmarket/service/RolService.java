package com.example.ggmarket.service;

import com.example.ggmarket.model.Rol;
import com.example.ggmarket.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    /**
     * Busca un rol por su nombre.
     *
     * @param nombre Nombre del rol a buscar.
     * @return Rol encontrado o null si no existe.
     */
    public Rol findByNombre(String nombre) {
        return rolRepository.findByNombre(nombre).orElse(null);
    }
}
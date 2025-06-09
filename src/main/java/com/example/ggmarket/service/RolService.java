package com.example.ggmarket.service;

import com.example.ggmarket.model.Rol;
import com.example.ggmarket.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    public Rol findByNombre(String nombre) {
        Rol rol = rolRepository.findByNombre(nombre);
        if (rol != null) {
            return rol;
        }
        return null; // o lanzar una excepción si prefieres
    }
}
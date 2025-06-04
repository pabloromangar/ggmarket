package com.example.ggmarket.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ggmarket.model.ProductoDigital;
import com.example.ggmarket.service.IGDBService;
import com.example.ggmarket.service.ProductoDigitalService;

@RestController
@RequestMapping("/api/productos/digitales")
public class ProductoDigitalApiController {

    private final ProductoDigitalService productoDigitalService;

    public ProductoDigitalApiController(ProductoDigitalService productoDigitalService) {
        this.productoDigitalService = productoDigitalService;
    }

    @GetMapping("/buscar")
    public List<ProductoDigital> buscarPorNombre(@RequestParam String nombre) {
        if (nombre.length() < 3) {
            return List.of();
        }
        return productoDigitalService.buscarPorNombre(nombre);
    }

    @Autowired
    private IGDBService igdbService;

    @GetMapping("/cover")
    public ResponseEntity<String> getCover(@RequestParam String name) {
        String url = igdbService.getCoverUrl(name);
        return url != null ? ResponseEntity.ok(url)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cover not found");
    }

}

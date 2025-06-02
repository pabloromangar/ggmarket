package com.example.ggmarket.controller;

import com.example.ggmarket.model.ClaveDigital;
import com.example.ggmarket.model.ProductoDigital;
import com.example.ggmarket.service.ClaveDigitalService;
import com.example.ggmarket.service.ProductoDigitalService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/productos/digitales")
public class ProductoDigitalController {
    @Autowired
    private ProductoDigitalService productoDigitalService;

    @Autowired
    private ClaveDigitalService claveDigitalService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", productoDigitalService.findAll());
        return "productos/digitales/lista";
    }

   

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("producto", new ProductoDigital());
        return "productos/digitales/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute ProductoDigital producto) {
        productoDigitalService.crearProducto(producto);
        return "redirect:/productos/digitales";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        var producto = productoDigitalService.findById(id);
        model.addAttribute("producto", producto);
        return "productos/digitales/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        productoDigitalService.deleteById(id);
        return "redirect:/productos/digitales";
    }

    @PostMapping("/{id}/claves")
    public String subirClaves(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        ProductoDigital producto = productoDigitalService.findById(id);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            List<ClaveDigital> claves = new ArrayList<>();
            String linea;
            boolean primera = true;
            while ((linea = reader.readLine()) != null) {
                if (primera) {
                    primera = false;
                    continue;
                } // saltar encabezado
                ClaveDigital clave = new ClaveDigital();
                clave.setClave(linea.trim());
                clave.setProductoDigital(producto);
                claves.add(clave);
            }
            claveDigitalService.guardarTodas(claves);
        } catch (IOException e) {
            e.printStackTrace(); // log real en prod
        }

        return "redirect:/productos/digitales";
    }

    @PostMapping("{id}/importar-claves")
    public String importarClaves(@PathVariable Long id,
            @RequestParam("archivo") MultipartFile archivo) {
        claveDigitalService.importarDesdeCSV(id, archivo);
        return "redirect:/productos/digitales";
    }

    @GetMapping("/{id}/importar-claves")
    public String mostrarFormularioImportacion(@PathVariable Long id, Model model) {
        ProductoDigital producto = productoDigitalService.findById(id);
        model.addAttribute("producto", producto);
        return "productos/digitales/importar_claves";
    }

}

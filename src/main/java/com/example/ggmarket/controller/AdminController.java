package com.example.ggmarket.controller;

import com.example.ggmarket.model.ClaveDigital;
import com.example.ggmarket.model.ProductoDigital;
import com.example.ggmarket.service.ClaveDigitalService;
import com.example.ggmarket.service.ProductoDigitalService;
import com.example.ggmarket.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProductoDigitalService productoDigitalService;

    @Autowired
    private ClaveDigitalService claveDigitalService;

    /**
     * Muestra el panel principal de administración.
     */
    @GetMapping
    public String panelAdmin(Model model) {
        return "admin/panel";
    }

    /**
     * Muestra la lista de usuarios registrados.
     */
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.findAll());
        return "admin/usuarios";
    }

    /**
     * Muestra la lista de productos digitales disponibles.
     */
    @GetMapping("/productos")
    public String listarProductos(Model model) {
        model.addAttribute("productos", productoDigitalService.findAll());
        return "productos/digitales/lista";
    }

    /**
     * Muestra el formulario para crear un nuevo producto digital.
     */
    @GetMapping("/productos/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("producto", new ProductoDigital());
        return "productos/digitales/formulario";
    }

    /**
     * Guarda o actualiza un producto digital con una imagen asociada.
     *
     * @param producto Producto digital a guardar.
     * @param imagenFile Imagen del producto.
     */
    @PostMapping("/productos/guardar")
    public String guardar(@ModelAttribute ProductoDigital producto, @RequestParam("imagenFile") MultipartFile imagenFile) {
        try {
            productoDigitalService.saveOrUpdateWithImage(producto, imagenFile);
        } catch (IOException e) {
            e.printStackTrace();
            return "productos/digitales/formulario";
        }
        return "redirect:/admin/productos";
    }

    /**
     * Muestra el formulario para editar un producto digital existente.
     *
     * @param id ID del producto digital.
     */
    @GetMapping("/productos/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        ProductoDigital producto = productoDigitalService.findById(id);
        model.addAttribute("producto", producto);
        return "productos/digitales/formulario";
    }

    /**
     * Elimina un producto digital por su ID.
     *
     * @param id ID del producto digital.
     */
    @GetMapping("/productos/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        productoDigitalService.deleteById(id);
        return "redirect:/admin/productos";
    }

    /**
     * Sube claves digitales desde un archivo de texto para un producto específico.
     *
     * @param id ID del producto digital.
     * @param file Archivo con claves, una por línea.
     */
    @PostMapping("/productos/{id}/claves")
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
                }
                ClaveDigital clave = new ClaveDigital();
                clave.setClave(linea.trim());
                clave.setProductoDigital(producto);
                claves.add(clave);
            }
            claveDigitalService.guardarTodas(claves);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/admin/productos";
    }

    /**
     * Importa claves digitales desde un archivo CSV para un producto digital.
     *
     * @param id ID del producto digital.
     * @param archivo Archivo CSV con las claves.
     */
    @PostMapping("/productos/{id}/importar-claves")
    public String importarClaves(@PathVariable Long id, @RequestParam("archivo") MultipartFile archivo) {
        claveDigitalService.importarDesdeCSV(id, archivo);
        return "redirect:/admin/productos";
    }

    /**
     * Muestra el formulario para importar claves digitales desde archivo CSV.
     *
     * @param id ID del producto digital.
     */
    @GetMapping("/productos/{id}/importar-claves")
    public String mostrarFormularioImportacion(@PathVariable Long id, Model model) {
        ProductoDigital producto = productoDigitalService.findById(id);
        model.addAttribute("producto", producto);
        return "productos/digitales/importar-claves";
    }
}

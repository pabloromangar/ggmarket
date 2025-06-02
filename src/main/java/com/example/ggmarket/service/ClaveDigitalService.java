package com.example.ggmarket.service;

import com.example.ggmarket.model.ClaveDigital;
import com.example.ggmarket.model.ProductoDigital;
import com.example.ggmarket.repository.ClaveDigitalRepository;
import com.example.ggmarket.repository.ProductoDigitalRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClaveDigitalService {

    @Autowired
    private ClaveDigitalRepository claveDigitalRepository;

    @Autowired
    private  ProductoDigitalRepository productoDigitalRepository;

    public List<ClaveDigital> obtenerPorProducto(ProductoDigital producto) {
        return claveDigitalRepository.findByProductoDigital(producto);
    }

    public long contarDisponiblesPorProducto(ProductoDigital producto) {
        return claveDigitalRepository.countByProductoDigitalAndUsadaFalse(producto);
    }

    public void guardarTodas(List<ClaveDigital> claves) {
        claveDigitalRepository.saveAll(claves);
    }

    public void guardar(ClaveDigital clave) {
        claveDigitalRepository.save(clave);
    }

    public void importarDesdeCSV(Long productoId, MultipartFile archivo) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(archivo.getInputStream()))) {
            ProductoDigital producto = productoDigitalRepository.findById(productoId)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            List<ClaveDigital> claves = new ArrayList<>();
            String linea;
            while ((linea = br.readLine()) != null) {
                ClaveDigital clave = new ClaveDigital();
                clave.setClave(linea.trim());
                clave.setProductoDigital(producto);
                clave.setUsada(false);
                claves.add(clave);
            }

            claveDigitalRepository.saveAll(claves);
        } catch (IOException e) {
            throw new RuntimeException("Error procesando archivo CSV", e);
        }
    }

}

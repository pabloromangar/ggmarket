package com.example.ggmarket.service;

import com.example.ggmarket.dto.CarritoItemDTO;
import com.example.ggmarket.model.Carrito;
import com.example.ggmarket.repository.CarritoRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarritoViewService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Transactional(readOnly = true) // Transacción de solo lectura, es más eficiente
    public int contarItemsUnicos(String emailUsuario) {
        return carritoRepository.countByUsuario_Email(emailUsuario);
    }

    // En CarritoViewService.java

    // ...

    // Método para obtener los items del carrito de un usuario
    public List<Carrito> getItemsDelUsuario(String email) {
        return carritoRepository.findByUsuario_Email(email); // Necesitarás crear este método en el repo
    }

    // En CarritoViewService.java

// ...

public Map<String, Object> getContenidoCarritoDTO(String email) {
    List<Carrito> items = getItemsDelUsuario(email);
    
    List<CarritoItemDTO> dtos = items.stream()
        .map(item -> new CarritoItemDTO(
            item.getProductoDigital().getId(),
            item.getProductoDigital().getNombre(),
            item.getProductoDigital().getImagenUrl(),
            item.getCantidad(),
            item.getProductoDigital().getPrecio(), // <-- Ahora es un double
            "Vendedor Ejemplo"
        )).collect(Collectors.toList());

    // --- CÁLCULO CON DOUBLE ---
    // Usamos mapToDouble y sum() para una operación de suma simple.
    double total = dtos.stream()
        .mapToDouble(item -> item.getPrecio() * item.getCantidad())
        .sum();

    Map<String, Object> response = new HashMap<>();
    response.put("items", dtos);
    response.put("total", total);
    
    return response;
}
}
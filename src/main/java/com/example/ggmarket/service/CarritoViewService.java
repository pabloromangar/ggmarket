package com.example.ggmarket.service;

import com.example.ggmarket.dto.CarritoItemDTO;
import com.example.ggmarket.model.Carrito;
import com.example.ggmarket.model.Usuario;
import com.example.ggmarket.repository.CarritoRepository;
import com.example.ggmarket.repository.UsuarioRepository;

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
    private UsuarioRepository usuarioRepository;
    
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
        // Primero encontramos al usuario
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado para obtener items del carrito"));
        
        // Luego usamos el nuevo método del repositorio
        return carritoRepository.findByUsuario(usuario);
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
                        item.getProductoDigital().getPrecio(),
                        "Vendedor Ejemplo" // Puedes cambiar esto si tienes un vendedor real
                )).collect(Collectors.toList());

        double total = dtos.stream()
                .mapToDouble(item -> item.getPrecio() * item.getCantidad())
                .sum();

        // === LA LÍNEA QUE AÑADIMOS PARA ARREGLAR EL ERROR ===
        int totalItems = dtos.size();
        // ======================================================

        Map<String, Object> response = new HashMap<>();
        response.put("items", dtos);
        response.put("total", total);
        response.put("totalItems", totalItems); // <-- AHORA SÍ INCLUIMOS LA CLAVE

        return response;
    }
}
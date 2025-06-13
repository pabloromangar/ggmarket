package com.example.ggmarket.repository;

import com.example.ggmarket.model.Pedido;
import com.example.ggmarket.model.Usuario; // Asegúrate de importar Usuario
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List; // Asegúrate de importar List

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    /**
     * Busca todos los pedidos asociados a un usuario específico,
     * ordenándolos por fecha de creación de forma descendente (los más nuevos primero).
     * Spring Data JPA crea la consulta automáticamente a partir del nombre del método.
     */
    List<Pedido> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario);
    
}
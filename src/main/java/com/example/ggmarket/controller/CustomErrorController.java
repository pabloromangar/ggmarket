package com.example.ggmarket.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    /**
     * Este método se mapea a la ruta "/error", que es a donde Spring Boot
     * reenvía todas las excepciones y errores HTTP por defecto.
     *
     * @param request La solicitud HTTP que causó el error.
     * @return El nombre de la plantilla de error a renderizar.
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        // 1. Obtenemos el código de estado del error (ej. 403, 404, 500).
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            // 2. Comparamos el código de estado para decidir qué página mostrar.
            if (statusCode == HttpStatus.FORBIDDEN.value()) {
                // Si el error es 403, devolvemos la ruta a nuestra nueva plantilla.
                return "403";
            }
        }
        
        // 3. Para cualquier otro error, devolvemos una página de error genérica.
        return "error"; // Asumiendo que tienes un error/error.html
    }
}
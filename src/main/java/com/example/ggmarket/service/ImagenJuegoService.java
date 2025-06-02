package com.example.ggmarket.service;

import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ImagenJuegoService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String apiKey = "c2912b4107374fe091691d04dee33633";

    public String obtenerImagenPorNombre(String nombreJuego) {
        String url = "https://api.rawg.io/api/games?search=" + UriUtils.encode(nombreJuego, StandardCharsets.UTF_8) + "&key=" + apiKey;
        
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // Aquí parseamos el JSON para extraer la URL de la imagen
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                JsonNode results = root.path("results");
                if (results.isArray() && results.size() > 0) {
                    JsonNode firstResult = results.get(0);
                    return firstResult.path("background_image").asText();
                }   
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // o URL por defecto
    }
}


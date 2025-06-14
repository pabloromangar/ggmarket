package com.example.ggmarket.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio para gestionar la subida de archivos a Cloudinary.
 * Se inicializa con las credenciales obtenidas desde application.properties.
 */
@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    /**
     * Constructor que inicializa el cliente de Cloudinary.
     * Utiliza la anotación @Value para inyectar las credenciales directamente
     * desde el archivo application.properties.
     *
     * @param cloudName El nombre de tu nube de Cloudinary.
     * @param apiKey    Tu API Key de Cloudinary.
     * @param apiSecret Tu API Secret de Cloudinary.
     */
    public CloudinaryService(@Value("${cloudinary.cloud_name}") String cloudName,
                             @Value("${cloudinary.api_key}") String apiKey,
                             @Value("${cloudinary.api_secret}") String apiSecret) {
        
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        
        // Inicializamos el objeto Cloudinary con la configuración
        this.cloudinary = new Cloudinary(config);
    }

    /**
     * Sube un archivo a Cloudinary.
     *
     * @param file El archivo (imagen) subido por el usuario, encapsulado en un MultipartFile.
     * @return La URL segura (https) donde el archivo ha sido almacenado.
     * @throws IOException si ocurre un error durante la subida del archivo.
     */
    public String uploadFile(MultipartFile file) throws IOException {
        try {
            // Obtenemos los bytes del archivo subido
            byte[] fileBytes = file.getBytes();

            // Usamos el uploader de Cloudinary para subir los bytes.
            // ObjectUtils.emptyMap() se usa si no necesitamos pasar opciones de transformación adicionales.
            Map uploadResult = cloudinary.uploader().upload(fileBytes, ObjectUtils.emptyMap());

            // El resultado de la subida es un Map que contiene mucha información.
            // La URL segura (https) se encuentra bajo la clave "secure_url".
            // La devolvemos como un String.
            return (String) uploadResult.get("secure_url");

        } catch (IOException e) {
            // En caso de un error de red o de lectura del archivo, relanzamos la excepción
            // para que el controlador pueda manejarla y mostrar un mensaje de error al usuario.
            throw new IOException("Error al subir el archivo a Cloudinary", e);
        }
    }
}
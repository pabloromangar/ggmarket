package com.example.ggmarket.service;

import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IGDBService {

    @Value("${igdb.client-id}")
    private String clientId;

    @Value("${igdb.token}")
    private String token;

    @Value("${igdb.api-url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getValidCoverUrl(String coverId) {
        String urlCoverBig = "https://images.igdb.com/igdb/image/upload/t_cover_big/" + coverId + ".jpg";
        if (urlExists(urlCoverBig)) {
            return urlCoverBig;
        } else if (urlExists("https://images.igdb.com/igdb/image/upload/t_thumb/" + coverId + ".jpg")) {
            String urlThumb = "https://images.igdb.com/igdb/image/upload/t_thumb/" + coverId + ".jpg";
            return urlThumb;
        }else{
            return null;
        }
    }

    private boolean urlExists(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setRequestMethod("HEAD");
            huc.setConnectTimeout(3000);
            huc.setReadTimeout(3000);
            int responseCode = huc.getResponseCode();
            return (responseCode == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            return false;
        }
    }

    public String getCoverUrl(String gameName) {
        // Paso 1: Obtener cover ID
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.set("Client-ID", clientId);
        headers.set("Authorization", "Bearer " + token);

        String body = String.format("search \"%s\";\nfields cover;\nlimit 1;", gameName);
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> gameResponse = restTemplate.exchange(
                apiUrl + "/games", HttpMethod.POST, request, String.class);

        JSONArray games = new JSONArray(gameResponse.getBody());
        if (games.isEmpty() || games.getJSONObject(0).isNull("cover")) {
            return null;
        }
        int coverId = games.getJSONObject(0).getInt("cover");

        // Paso 2: Obtener image_id
        String coverQuery = String.format("fields image_id;\nwhere id = %d;", coverId);
        HttpEntity<String> coverRequest = new HttpEntity<>(coverQuery, headers);

        ResponseEntity<String> coverResponse = restTemplate.exchange(
                apiUrl + "/covers", HttpMethod.POST, coverRequest, String.class);

        JSONArray covers = new JSONArray(coverResponse.getBody());
        if (covers.isEmpty()) {
            return null;
        }

        String imageId = covers.getJSONObject(0).getString("image_id");
        return this.getValidCoverUrl(imageId);
    }
}

package com.edu.eci.DrawSync.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    public String getRandomWord() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content",
                    "Genera una única palabra en español que sea un sustantivo concreto (objeto físico) fácil de dibujar para un juego de Pictionary. Ejemplos: 'Bicicleta', 'Pizza', 'Castillo'. Solo responde con la palabra, sin puntos ni explicaciones.");

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-4o-mini");
            requestBody.put("messages", Collections.singletonList(message));
            requestBody.put("temperature", 0.9);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    OPENAI_API_URL,
                    org.springframework.http.HttpMethod.POST,
                    request,
                    new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {
                    });

            if (response.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> messageResponse = (Map<String, Object>) choices.get(0).get("message");
                    String content = (String) messageResponse.get("content");
                    return content.trim().replace(".", "");
                }
            }
        } catch (Exception e) {
            System.err.println("Error calling OpenAI API (getRandomWord): " + e.getMessage());
            e.printStackTrace();
        }
        // Fallback list
        String[] fallbacks = { "Casa", "Gato", "Perro", "Sol", "Flor", "Carro", "Avion", "Pelota", "Arbol", "Libro" };
        return fallbacks[new Random().nextInt(fallbacks.length)];
    }

    public int evaluateDrawing(String base64Image, String word) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> textContent = new HashMap<>();
            textContent.put("type", "text");
            textContent.put("text", "Evalúa qué tan bien este dibujo representa la palabra: '" + word
                    + "'. Asigna un puntaje de 0 a 100 basándote en el parecido, los detalles y la creatividad. Sé crítico y usa todo el rango de 0 a 100 para diferenciar buenos dibujos de malos. Responde SOLO con el número entero, nada más.");

            Map<String, Object> imageContent = new HashMap<>();
            imageContent.put("type", "image_url");
            Map<String, String> imageUrl = new HashMap<>();
            imageUrl.put("url", base64Image);
            imageContent.put("image_url", imageUrl);

            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", Arrays.asList(textContent, imageContent));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-4o");
            requestBody.put("messages", Collections.singletonList(message));
            requestBody.put("max_tokens", 10);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    OPENAI_API_URL,
                    org.springframework.http.HttpMethod.POST,
                    request,
                    new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {
                    });

            if (response.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> messageResponse = (Map<String, Object>) choices.get(0).get("message");
                    String content = (String) messageResponse.get("content");
                    System.out.println("OpenAI Response for drawing: " + content);
                    try {
                        return Integer.parseInt(content.trim());
                    } catch (NumberFormatException e) {
                        System.err.println("Failed to parse score from OpenAI: " + content);
                        return 0;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error calling OpenAI API (evaluateDrawing): " + e.getMessage());
            // Fallback for when API is down or quota exceeded
            System.out.println("Using fallback random score due to API error.");
            return 40 + new Random().nextInt(56); // Random score between 40 and 95
        }
        return 0; // Should not be reached given the fallback above
    }
}

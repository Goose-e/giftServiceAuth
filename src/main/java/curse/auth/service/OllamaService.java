package curse.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import curse.auth.dto.ollama.OllamaGiftResponse;
import curse.auth.models.Victim;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class OllamaService implements IOllamaService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public OllamaGiftResponse analyzeVictim(Victim victim) {
        try {
            String prompt = buildPrompt(victim);

            String body = """
                    {
                      "model": "llama3.1",
                      "prompt": %s,
                      "format": "json",
                      "stream": false
                    }
                    """.formatted(objectMapper.writeValueAsString(prompt));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:11434/api/generate"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            JsonNode root = objectMapper.readTree(response.body());

            String aiJson = root.get("response").asText();

            return objectMapper.readValue(aiJson, OllamaGiftResponse.class);

        } catch (Exception e) {
            throw new RuntimeException("Ollama error: " + e.getMessage(), e);
        }
    }

    private String buildPrompt(Victim victim) {
        Long age = null;

        if (victim.getBirthdate() != null) {
            age = ChronoUnit.YEARS.between(
                    victim.getBirthdate().toLocalDate(),
                    LocalDate.now()
            );
        }

        return """
                Ты эксперт по подбору подарков.

                Проанализируй анкету и верни ТОЛЬКО корректный JSON без markdown и пояснений.

                Формат:
                {
                  "tags": ["tech", "gaming", "music"],
                  "ideas": [
                    {"name": "название подарка", "price": 100},
                    {"name": "...", "price": 50}
                  ],
                  "reason": "объяснение"
                }

                Правила:
                - 5 тегов
                - 5-10 конкретных подарков
                - НЕ добавляй текст вне JSON
                - названия подарков реальные и понятные
                - price указывай целым числом >= 0

                Анкета:
                Пол: %s
                Возраст: %s
                Страна: %s
                Город: %s
                Информация: %s
                """.formatted(
                victim.getGender() == null ? "не указано" : victim.getGender(),
                age == null ? "не указан" : age,
                victim.getCountry() == null ? "не указано" : victim.getCountry(),
                victim.getCity() == null ? "не указано" : victim.getCity(),
                victim.getInfo() == null ? "не указано" : victim.getInfo()
        );
    }
}

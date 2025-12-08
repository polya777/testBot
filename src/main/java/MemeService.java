import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.logging.Logger;
import java.util.function.Function;
import java.io.IOException;

public class MemeService {
    private   final Logger logger = Logger.getLogger(MemeService.class.getName());
    private   final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient;

    public MemeService() {
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public   String getRandomCatImageUrl() {
        String imageUrl = tryPrimaryApi();
        if (imageUrl != null) {
            return imageUrl;
        }

        logger.warning("Основной API не доступен, используется альтернативный");
        return tryAlternativeApi();
    }

    private String tryPrimaryApi() {
        return fetchImageUrl(
                "https://api.thecatapi.com/v1/images/search?mime_types=jpg,png",
                "Основной API",
                jsonNode -> {
                    if (jsonNode.isArray() && jsonNode.size() > 0) {
                        return jsonNode.get(0).get("url").asText();
                    }
                    return null;
                },
                null // Без префикса
        );
    }

    private String tryAlternativeApi() {
        return fetchImageUrl(
                "https://cataas.com/cat?json=true",
                "Альтернативный API",
                jsonNode -> jsonNode.get("url").asText(),
                "https://cataas.com" // Префикс для URL
        );
    }

    private String fetchImageUrl(String url, String apiName,
                                 Function<JsonNode, String> urlExtractor,
                                 String urlPrefix) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode jsonNode = objectMapper.readTree(response.body());
                String extractedUrl = urlExtractor.apply(jsonNode);

                if (extractedUrl != null) {
                    String fullUrl = (urlPrefix != null) ? urlPrefix + extractedUrl : extractedUrl;
                    logger.info(String.format("Получено изображение из %s: %s", apiName, fullUrl));
                    return fullUrl;
                }
            } else {
                logger.warning(String.format("Ошибка %s: статус код %d", apiName, response.statusCode()));
            }

        } catch (IOException e) {
            logger.warning(String.format("Ошибка ввода-вывода при запросе к %s: %s", apiName, e.getMessage()));
        } catch (InterruptedException e) {
            logger.warning(String.format("Запрос к %s был прерван: %s", apiName, e.getMessage()));
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.warning(String.format("Ошибка %s: %s", apiName, e.getMessage()));
        }

        return null;
    }
}
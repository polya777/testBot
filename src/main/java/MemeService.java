/*import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MemeService {
    private static final Random random = new Random();

    private static final List<String> MEME_URLS = Arrays.asList(
            "https://memesapi.vercel.app/"
    );

    public static String getRandomMemeUrl() {
        return MEME_URLS.get(random.nextInt(MEME_URLS.size()));
    }
}*/
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.io.IOException;
import java.util.logging.Logger;

public class MemeService {
    private static final Logger logger = Logger.getLogger(MemeService.class.getName());
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static HttpClient httpClient;

    public MemeService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public static String getRandomCatImageUrl() {
        String imageUrl = tryPrimaryApi();
        if (imageUrl != null) {
            return imageUrl;
        }

        logger.warning("Основной API не доступен, используется альтернативный");
        return tryAlternativeApi();
    }

    private static String tryPrimaryApi() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.thecatapi.com/v1/images/search?mime_types=jpg,png"))
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode jsonNode = objectMapper.readTree(response.body());
                if (jsonNode.isArray() && jsonNode.size() > 0) {
                    String url = jsonNode.get(0).get("url").asText();
                    logger.info("Получено изображение из основного API: " + url);
                    return url;
                }
            }
        } catch (Exception e) {
            logger.warning("Ошибка основного API: " + e.getMessage());
        }
        return null;
    }

    private static String tryAlternativeApi() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://cataas.com/cat?json=true"))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode jsonNode = objectMapper.readTree(response.body());
                String url = "https://cataas.com" + jsonNode.get("url").asText();
                logger.info("Получено изображение из альтернативного API: " + url);
                return url;
            }
        } catch (Exception e) {
            logger.severe("Ошибка альтернативного API: " + e.getMessage());
        }
        return "https://cataas.com/cat"; // Fallback URL
    }
}
package Bot.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static Properties properties = new Properties();

    static {
        try (InputStream is = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) {
                properties.load(is);
                System.out.println("Loaded config from config.properties");
            } else {
                System.out.println("config.properties not found in classpath");
            }
        } catch (IOException e) {
            System.err.println("Error loading config.properties: " + e.getMessage());
        }
    }

    public static String getBotToken() {
        String token = System.getenv("BOT_TOKEN");
        if (token != null && !token.trim().isEmpty()) {
            System.out.println("Using BOT_TOKEN from environment");
            return token.trim();
        }

        token = properties.getProperty("bot.token");
        if (token != null && !token.trim().isEmpty()) {
            System.out.println("Using bot.token from config.properties");
            return token.trim();
        }

        throw new RuntimeException(
                "Bot token not found! Set BOT_TOKEN environment variable or config.properties"
        );
    }

    public static String getBotUsername() {
        String username = System.getenv("BOT_USERNAME");
        if (username != null && !username.trim().isEmpty()) {
            System.out.println("Using BOT_USERNAME from environment");
            return username.trim();
        }

        username = properties.getProperty("bot.username");
        if (username != null && !username.trim().isEmpty()) {
            System.out.println("Using bot.username from config.properties");
            return username.trim();
        }

        throw new RuntimeException(
                "Bot username not found! Set BOT_USERNAME environment variable or config.properties"
        );
    }

    public static String getCatApiUrl() {
        return properties.getProperty("cat.api.url",
                "https://api.thecatapi.com/v1/images/search");
    }

    public static String getAlternativeCatApiUrl() {
        return properties.getProperty("cat.alternative.api.url",
                "https://cataas.com/cat");
    }
}
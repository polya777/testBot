import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MemeService {
    private static final Random random = new Random();

    private static final List<String> MEME_URLS = Arrays.asList(
            "https://github.com/httpcats/http.cat/tree/master/public/images-original"
    );

    public static String getRandomMemeUrl() {
        return MEME_URLS.get(random.nextInt(MEME_URLS.size()));
    }
}
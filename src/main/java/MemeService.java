import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MemeService {
    private static final Random random = new Random();

    private static final List<String> MEME_URLS = Arrays.asList(
            "https://i.redd.it/utixv6nqbgtd1.jpeg",
            "https://i.redd.it/0p3d88a3nftd1.jpeg"
    );

    public static String getRandomMemeUrl() {
        return MEME_URLS.get(random.nextInt(MEME_URLS.size()));
    }
}
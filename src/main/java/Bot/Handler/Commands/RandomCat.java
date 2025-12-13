package Bot.Handler.Commands;
import Bot.Service.MemeService;
import Bot.SimpleMemeBot;
import Bot.Handler.Commands.Interfaces.Command;
import java.util.logging.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

public class RandomCat implements Command{
    private final TelegramLongPollingBot bot;
    private static final Logger logger = Logger.getLogger(SimpleMemeBot.class.getName());
    private final MemeService MemeService = new MemeService();

    public RandomCat(TelegramLongPollingBot bot) {
        this.bot = bot;
    }

    @Override
    public String InitMessage() {
        return "Ищу котика...";
    }

    public void sendRandomCatMeme(Long chatId) {
        try {
            logger.info(String.format("Запрос изображения кота для чата: %s", chatId));
            String imageUrl = MemeService.getRandomCatImageUrl();

            SendPhoto photo = new SendPhoto();
            photo.setChatId(chatId.toString());
            photo.setPhoto(new InputFile(imageUrl));
            photo.setCaption("Вот ваш случайный кот!\nХотите еще? Просто отправьте /cat");

            bot.execute(photo);
            logger.info(String.format("Изображение кота отправлено в чат: %s", chatId));

        } catch (Exception e) {
            logger.severe(String.format("Ошибка при отправке изображения в чат %s: %s", chatId, e.getMessage()));
        }
    }
}
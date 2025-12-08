import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.logging.Logger;

public class SimpleMemeBot extends TelegramLongPollingBot {
    private static final Logger logger = Logger.getLogger(SimpleMemeBot.class.getName());
    private final MemeService MemeService = new MemeService();
    @Override
    public String getBotUsername() {

        return Config.getBotUsername();
    }

    @Override
    public String getBotToken() {

        return Config.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    sendText(chatId, "Привет! Я бот для мемов. Напиши /meme чтобы получить мем!");
                    break;
                case "/meme":
                    sendRandomCatMeme(chatId);
                    break;
                case "/help":
                    sendText(chatId, "Напиши /meme для мема или /start для начала");
                    break;
                default:
                    sendText(chatId, "Не понимаю. Используйте /help");
            }
        }
    }


    private void sendRandomCatMeme(Long chatId) {
        try {
            logger.info(String.format("Запрос изображения кота для чата: %s", chatId));
            String imageUrl = MemeService.getRandomCatImageUrl();

            SendPhoto photo = new SendPhoto();
            photo.setChatId(chatId.toString());
            photo.setPhoto(new InputFile(imageUrl));
            photo.setCaption("Вот ваш случайный кот!\nХотите еще? Просто отправьте /meme");

            execute(photo);
            logger.info(String.format("Изображение кота отправлено в чат: %s", chatId));

        } catch (Exception e) {
            logger.severe(String.format("Ошибка при отправке изображения в чат %s: %s", chatId, e.getMessage()));
            sendText(chatId, "Не удалось загрузить изображение кота. Попробуйте позже.");
        }
    }

    private void sendText(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
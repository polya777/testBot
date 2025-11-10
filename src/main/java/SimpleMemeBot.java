import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import resources.config.properties;

public class SimpleMemeBot extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return bot.username;
    }

    @Override
    public String getBotToken() {

        return "8244351876:AAFWE2m1Fr5pZ1Cl_ULKlQa2n2vksWvFaAM";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if (messageText.equals("/start")) {
                sendText(chatId, "Привет! Я бот для мемов. Напиши /meme чтобы получить мем!");
            }
            else if (messageText.equals("/meme") || messageText.equals("мем")) {
                sendMeme(chatId);
            }
            else {
                sendText(chatId, "Не понимаю. Напиши /meme для мема или /start для начала");
            }
        }
    }

    private void sendMeme(Long chatId) {
        try {
            String memeUrl = MemeService.getRandomMemeUrl();
            InputFile photo = new InputFile(memeUrl);

            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chatId.toString());
            sendPhoto.setPhoto(photo);
            sendPhoto.setCaption("Вот твой мем! 🎭");

            execute(sendPhoto);
            System.out.println("Мем отправлен: " + memeUrl);

        } catch (Exception e) {
            sendText(chatId, "Ошибка при отправке мема 😢");
            e.printStackTrace();
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
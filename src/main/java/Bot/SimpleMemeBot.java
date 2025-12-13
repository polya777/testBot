package Bot;
import Bot.Handler.BotHandler;
import Bot.Service.Config;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class SimpleMemeBot extends TelegramLongPollingBot {
    private final BotHandler botHandler;

    public SimpleMemeBot() {
        this.botHandler = new BotHandler(this);
    }

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
        botHandler.handleUpdate(update);
    }
}
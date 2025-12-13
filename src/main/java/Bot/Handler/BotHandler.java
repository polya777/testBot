package Bot.Handler;

import Bot.Handler.Commands.HelpCommand;
import Bot.Handler.Commands.RPS.RPSGame;
import Bot.Handler.Commands.RandomCat;
import Bot.Handler.Commands.SendStartMenu;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

public class BotHandler {
    private final TelegramLongPollingBot bot;
    private final Buttons buttons;
    private final RPSGame rpsGame;
    private final RandomCat randomCat;
    private final SendStartMenu start;
    private final HelpCommand help;

    public BotHandler(TelegramLongPollingBot bot) {
        this.bot = bot;
        this.buttons = new Buttons(bot);
        this.rpsGame = new RPSGame(bot);
        this.randomCat = new RandomCat(bot);
        this.start = new SendStartMenu();
        this.help = new HelpCommand();
    }

    public void handleUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    buttons.sendTextWithKeyboard(chatId, start.InitMessage(), buttons.createMainMenuKeyboard());
                    break;
                case "/cat":
                case "Котик":
                    randomCat.sendRandomCatMeme(chatId);
                    break;
                case "/help":
                case "Помощь":
                    buttons.sendTextWithKeyboard(chatId, help.InitMessage(), buttons.createMainMenuKeyboard());
                    break;
                case "/rps":
                case "Камень-ножницы-бумага":
                    rpsGame.startGame(chatId);
                    break;
                case "/rock":
                case "Камень":
                    rpsGame.processRPSMove(chatId, "камень");
                    break;
                case "/scissors":
                case "Ножницы":
                    rpsGame.processRPSMove(chatId, "ножницы");
                    break;
                case "/paper":
                case "Бумага":
                    rpsGame.processRPSMove(chatId, "бумага");
                    break;
                default:
                    String message = "Не понимаю. Используйте команду /help или выберите команду:";
                    buttons.sendTextWithKeyboard(chatId, message, buttons.createMainMenuKeyboard());
            }
        }
    }
}

package Bot.Handler.Commands.RPS;
import Bot.Handler.Buttons;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import Bot.Handler.Commands.Interfaces.Command;
public class RPSGame implements Command {
    private static TelegramLongPollingBot bot;
    private Map<Long, RPSGameState> rpsGames = new HashMap<>();
    private final Buttons buttons;
    private final String[] choices = {"камень", "бумага", "ножницы"};

    public RPSGame(TelegramLongPollingBot bot) {
        this.bot = bot;
        this.buttons = new Buttons(bot);
    }

    @Override
    public String InitMessage() {

        return "Создание игры";
    }

    public void startGame(Long chatId) {

        buttons.sendTextWithKeyboard(chatId, startRPSGame(chatId), buttons.createRPSGameKeyboard());
    }

    private String startRPSGame(Long chatId) {
        RPSGameState state = new RPSGameState();
        rpsGames.put(chatId, state);
        String message = "*Камень-Ножницы-Бумага*\n\nИгра из 5 раундов\nПри равенстве - дополнительные раунды\n\nВыберите ваш ход:\nРаунд 1 из 5:";

        return message;
    }

    public void processRPSMove(Long chatId, String userChoice) {
        if (!rpsGames.containsKey(chatId)) {
            buttons.sendTextWithKeyboard(chatId, "Сначала начните игру: /rps", buttons.createRPSGameKeyboard());
            return;
        }

        RPSGameState state = rpsGames.get(chatId);

        Random rand = new Random();

        String botChoice = choices[rand.nextInt(3)];

        String result = determineRoundResult(userChoice, botChoice);

        if (result.equals("user")) {
            state.addUser();
        } else if (result.equals("bot")) {
            state.addBot();
        } else {
            state.addDraws();
        }

        String roundInfo = String.format("Вы: %s vs Бот: %s - %s", userChoice, botChoice,
                result.equals("user") ? "Вы победили" : result.equals("bot") ? "Бот победил" : "Ничья");
        state.addHistory(roundInfo);

        int totalRounds = state.getSizeHistory();
        String roundResult = String.format("Раунд %d:\n%s\n", totalRounds, roundInfo);

        if (!state.getExtraRound() && totalRounds >= 5) {
            if (state.getUserWins() == state.getBotWins()) {
                state.changeExtraRound();
                buttons.sendTextWithKeyboard(chatId, String.format("%sСчёт равен! Дополнительный раунд:", roundResult), buttons.createRPSGameKeyboard());
            } else {
                showResults(chatId, state);
                rpsGames.remove(chatId);
            }
        } else if (state.getExtraRound()) {
            if (state.getUserWins() > state.getBotWins() || state.getBotWins() > state.getUserWins()) {
                showResults(chatId, state);
                rpsGames.remove(chatId);
            } else {
                buttons.sendTextWithKeyboard(chatId, String.format("%sСнова ничья! Ещё один раунд:", roundResult), buttons.createRPSGameKeyboard());
            }
        } else {
            buttons.sendTextWithKeyboard(chatId, String.format("%sРаунд %d из 5:", roundResult, totalRounds + 1), buttons.createRPSGameKeyboard());
        }
    }

    private String determineRoundResult(String userChoice, String botChoice) {
        if (userChoice.equals(botChoice)) {
            return "draw";
        }

        if ((userChoice.equals("камень") && botChoice.equals("ножницы")) ||
                (userChoice.equals("ножницы") && botChoice.equals("бумага")) ||
                (userChoice.equals("бумага") && botChoice.equals("камень"))) {
            return "user";
        }

        return "bot";
    }

    private void showResults(Long chatId, RPSGameState state) {
        String winner;
        if (state.getUserWins() > state.getBotWins()) {
            winner = "Вы победили в игре!";
        } else if (state.getBotWins() > state.getUserWins()) {
            winner = "Бот победил в игре!";
        } else {
            winner = "Ничья в игре!";
        }

        String result = String.format("\n*Итоговый счёт:*\nВы: %d победы\nБот: %d победы\nНичьих: %d\n\n%s\n\nВозвращаемся в главное меню...", state.getUserWins(), state.getBotWins(), state.getDraws(), winner);

        buttons.sendTextWithKeyboard(chatId,result, buttons.createMainMenuKeyboard());
        rpsGames.remove(chatId);
    }
}

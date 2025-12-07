import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class SimpleMemeBot extends TelegramLongPollingBot {
    private static final Logger logger = Logger.getLogger(SimpleMemeBot.class.getName());
    private final MemeService MemeService = new MemeService();
    private Map<Long, RPSGameState> rpsGames = new HashMap<>();
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
                    sendText(chatId, "Привет! Я бот, который присылает котиков и может играть в камень-ножницы-бумага. Напиши /cat чтобы получить котика или /rps, чтобы сыграть в камень-ножницы-бумага.");
                    break;
                case "/cat":
                    sendRandomCatMeme(chatId);
                    break;
                case "/help":
                    sendText(chatId, "Напиши /cat для котика, /start для начала или /rps, чтобы сыграть в камень-ножницы-бумага");
                    break;
                case "/rps":
                    startRPSGame(chatId);
                    break;
                case "/rock":
                case "/scissors":
                case "/paper":
                    processRPSMove(chatId, messageText.substring(1)); // Убираем слеш
                    break;
                default:
                    sendText(chatId, "Не понимаю. Используйте /help");
            }
        }
    }




    private void sendRandomCatMeme(Long chatId) {
        try {
            logger.info("Запрос изображения кота для чата: " + chatId);
            String imageUrl = MemeService.getRandomCatImageUrl();

            SendPhoto photo = new SendPhoto();
            photo.setChatId(chatId.toString());
            photo.setPhoto(new InputFile(imageUrl));
            photo.setCaption("Вот ваш случайный кот!\nХотите еще? Просто отправьте /cat");

            execute(photo);
            logger.info("Изображение кота отправлено в чат: " + chatId);

        } catch (Exception e) {
            logger.severe("Ошибка при отправке изображения в чат " + chatId + ": " + e.getMessage());
            sendText(chatId, "Не удалось загрузить изображение кота. Попробуйте позже.");
        }
    }



    private void startRPSGame(Long chatId) {
        RPSGameState state = new RPSGameState();
        rpsGames.put(chatId, state);
        sendText(chatId, "Начинаем игру! 5 раундов. Пишите /rock - камень, /paper - бумага или /scissors - ножницы \nРаунд 1 из 5:");
    }

    private void processRPSMove(Long chatId, String userChoice) {
        if (!rpsGames.containsKey(chatId)) {
            sendText(chatId, "Сначала начните игру: /rps");
            return;
        }

        RPSGameState state = rpsGames.get(chatId);

        Random rand = new Random();
        String[] choices = {"rock", "paper", "scissors"};
        String botChoice = choices[rand.nextInt(3)];

        String result = determineRoundResult(userChoice, botChoice);

        if (result.equals("user")) {
            state.userWins++;
        } else if (result.equals("bot")) {
            state.botWins++;
        } else {
            state.draws++;
        }

        String roundInfo = String.format("Вы: %s vs Бот: %s - %s",
                userChoice, botChoice,
                result.equals("user") ? "Вы победили" :
                        result.equals("bot") ? "Бот победил" : "Ничья");
        state.history.add(roundInfo);

        int totalRounds = state.history.size();
        String roundResult = String.format("Раунд %d:\n%s\n", totalRounds, roundInfo);

        if (!state.isExtraRound && totalRounds >= 5) {
            if (state.userWins == state.botWins) {
                state.isExtraRound = true;
                sendText(chatId, roundResult + "Счёт равен! Дополнительный раунд:");
            } else {
                showResults(chatId, state);
                rpsGames.remove(chatId);
            }
        } else if (state.isExtraRound) {
            if (state.userWins > state.botWins || state.botWins > state.userWins) {
                showResults(chatId, state);
                rpsGames.remove(chatId);
            } else {
                sendText(chatId, roundResult + "Снова ничья! Ещё один раунд:");
            }
        } else {
            sendText(chatId, roundResult + "Раунд " + (totalRounds + 1) + " из 5:");
        }
    }

    private String determineRoundResult(String userChoice, String botChoice) {
        if (userChoice.equals(botChoice)) {
            return "draw";
        }

        if ((userChoice.equals("rock") && botChoice.equals("scissors")) ||
                (userChoice.equals("scissors") && botChoice.equals("paper")) ||
                (userChoice.equals("paper") && botChoice.equals("rock"))) {
            return "user";
        }

        return "bot";
    }

    private void showResults(Long chatId, RPSGameState state) {
        StringBuilder stats = new StringBuilder("Статистика игры:\n");

        for (int i = 0; i < state.history.size(); i++) {
            stats.append("Раунд ").append(i + 1).append(": ").append(state.history.get(i)).append("\n");
        }

        String winner;
        if (state.userWins > state.botWins) {
            winner = "Вы победили в игре!";
        } else if (state.botWins > state.userWins) {
            winner = "Бот победил в игре!";
        } else {
            winner = "Ничья в игре!";
        }

        String result = String.format("\nИтоговый счёт:\nВы: %d побед\nБот: %d побед\nНичьих: %d\n\n%s\n\nНовая игра: /rps",
                state.userWins, state.botWins, state.draws, winner);

        sendText(chatId, stats.toString() + result);
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



    private class RPSGameState {
        int userWins = 0;
        int botWins = 0;
        int draws = 0;
        List<String> history = new ArrayList<>();
        boolean isExtraRound = false;
    }
}
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

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
                    sendStartMenu(chatId);
                    break;
                case "/cat":
                case "Котик":
                    sendRandomCatMeme(chatId);
                    break;
                case "/help":
                case "Помощь":
                    sendHelpMessage(chatId);
                    break;
                case "/rps":
                case "Камень-ножницы-бумага":
                    startRPSGame(chatId);
                    break;
                case "/rock":
                case "Камень":
                    processRPSMove(chatId, "камень");
                    break;
                case "/scissors":
                case "Ножницы":
                    processRPSMove(chatId, "ножницы");
                    break;
                case "/paper":
                case "Бумага":
                    processRPSMove(chatId, "бумага");
                    break;
                default:
                    sendTextWithKeyboard(chatId, "Не понимаю. Используйте команду /help или выберите команду:", createMainMenuKeyboard());
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
            photo.setCaption("Вот ваш случайный кот!\nХотите еще? Просто отправьте /cat");

            execute(photo);
            logger.info(String.format("Изображение кота отправлено в чат: %s", chatId));

        } catch (Exception e) {
            logger.severe(String.format("Ошибка при отправке изображения в чат %s: %s", chatId, e.getMessage()));
            sendTextWithKeyboard(chatId, "Не удалось загрузить изображение кота. Попробуйте позже.", createMainMenuKeyboard());
        }
    }



    private void startRPSGame(Long chatId) {
        RPSGameState state = new RPSGameState();
        rpsGames.put(chatId, state);
        String message = "*Камень-Ножницы-Бумага*\n\nИгра из 5 раундов\nПри равенстве - дополнительные раунды\n\nВыберите ваш ход:\nРаунд 1 из 5:";

        sendTextWithKeyboard(chatId, message, createRPSGameKeyboard());
    }

    private void processRPSMove(Long chatId, String userChoice) {
        if (!rpsGames.containsKey(chatId)) {
            sendTextWithKeyboard(chatId, "Сначала начните игру: /rps", createRPSGameKeyboard());
            return;
        }

        RPSGameState state = rpsGames.get(chatId);

        Random rand = new Random();
        String[] choices = {"камень", "бумага", "ножницы"};
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
                result.equals("user") ? "Вы победили" : result.equals("bot") ? "Бот победил" : "Ничья");
        state.history.add(roundInfo);

        int totalRounds = state.history.size();
        String roundResult = String.format("Раунд %d:\n%s\n", totalRounds, roundInfo);

        if (!state.isExtraRound && totalRounds >= 5) {
            if (state.userWins == state.botWins) {
                state.isExtraRound = true;
                sendTextWithKeyboard(chatId, roundResult + "Счёт равен! Дополнительный раунд:", createRPSGameKeyboard());
            } else {
                showResults(chatId, state);
                rpsGames.remove(chatId);
            }
        } else if (state.isExtraRound) {
            if (state.userWins > state.botWins || state.botWins > state.userWins) {
                showResults(chatId, state);
                rpsGames.remove(chatId);
            } else {
                sendTextWithKeyboard(chatId, roundResult + "Снова ничья! Ещё один раунд:", createRPSGameKeyboard());
            }
        } else {
            sendTextWithKeyboard(chatId, roundResult + "Раунд " + (totalRounds + 1) + " из 5:", createRPSGameKeyboard());
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

        String result = String.format("\n*Итоговый счёт:*\nВы: %d победы\nБот: %d победы\nНичьих: %d\n\n%s\n", state.userWins, state.botWins, state.draws, winner);

        sendTextWithKeyboard(chatId,result + "\nВозвращаемся в главное меню...", createMainMenuKeyboard());
        rpsGames.remove(chatId);
    }

    private ReplyKeyboardMarkup createMainMenuKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Котик");
        row1.add("Камень-ножницы-бумага");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Помощь");

        keyboard.add(row1);
        keyboard.add(row2);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    private void sendStartMenu(Long chatId) {
        String welcomeMessage = "Добро пожаловать!\n\n" +
                "Я бот, который умеет:\n" +
                "- Присылать котиков\n" +
                "- Играть в камень-кожницы-бумага\n\n" +
                "Выберите действие:\n" +
                "/cat - получить случайного котика\n" +
                "/rps - сыграть в камень-ножницы-бумага\n" +
                "/help - помощь по боту";

        sendTextWithKeyboard(chatId, welcomeMessage, createMainMenuKeyboard());
    }

    private ReplyKeyboardMarkup createRPSGameKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("Камень");
        row.add("Ножницы");
        row.add("Бумага");

        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    private void sendHelpMessage(Long chatId) {
        String helpMessage = "*Помощь*\n\n" +
                "*Доступные команды:*\n" +
                "/cat - получить случайного котика\n" +
                "/rps - играть в Камень-Ножницы-Бумага\n\n" +
                "*Правила игры:*\n" +
                "- Камень бьет ножницы\n" +
                "- Ножницы бьют бумагу\n" +
                "- Бумага бьет камень\n" +
                "- 5 основных раундов\n" +
                "- При равенстве - дополнительные раунды";

        sendTextWithKeyboard(chatId, helpMessage, createMainMenuKeyboard());
    }

    private void sendTextWithKeyboard(Long chatId, String text, ReplyKeyboardMarkup keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setParseMode("Markdown");
        message.setReplyMarkup(keyboard);

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
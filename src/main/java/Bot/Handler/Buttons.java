package Bot.Handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.util.ArrayList;
import java.util.List;

public class Buttons {
    private ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
    private ReplyKeyboardMarkup keyboardMarkupRPS = new ReplyKeyboardMarkup();
    private static TelegramLongPollingBot bot;
    public Buttons(TelegramLongPollingBot bot) {
        this.bot = bot;
    }
    public ReplyKeyboardMarkup createMainMenuKeyboard() {
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

    public ReplyKeyboardMarkup createRPSGameKeyboard() {
        keyboardMarkupRPS.setSelective(true);
        keyboardMarkupRPS.setResizeKeyboard(true);
        keyboardMarkupRPS.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("Камень");
        row.add("Ножницы");
        row.add("Бумага");

        keyboard.add(row);

        keyboardMarkupRPS.setKeyboard(keyboard);
        return keyboardMarkupRPS;
    }

    public void sendTextWithKeyboard(Long chatId, String text, ReplyKeyboardMarkup keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setParseMode("Markdown");
        message.setReplyMarkup(keyboard);

        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

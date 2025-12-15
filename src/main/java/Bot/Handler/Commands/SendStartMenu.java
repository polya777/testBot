package Bot.Handler.Commands;

import Bot.Handler.Commands.Interfaces.Command;

public class SendStartMenu implements Command {
    @Override
    public String InitMessage() {
        return "Добро пожаловать!\n\nЯ бот, который умеет:\n- Присылать котиков\n- Играть в камень-кожницы-бумага\n\nВыберите действие:\n/cat - получить случайного котика\n/rps - сыграть в камень-ножницы-бумага\n/help - помощь по боту";
    }
}

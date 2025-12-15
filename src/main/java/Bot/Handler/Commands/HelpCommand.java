package Bot.Handler.Commands;

import Bot.Handler.Commands.Interfaces.Command;

public class HelpCommand implements Command {
    @Override
    public String InitMessage() {
        return "*Помощь*\n\n*Доступные команды:*\n/cat - получить случайного котика\n/rps - играть в Камень-Ножницы-Бумага\n\n*Правила игры:*\n- Камень бьет ножницы\n- Ножницы бьют бумагу\n- Бумага бьет камень\n- 5 основных раундов\n- При равенстве - дополнительные раунды";
    }
}

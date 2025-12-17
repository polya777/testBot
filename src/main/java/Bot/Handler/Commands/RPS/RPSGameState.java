package Bot.Handler.Commands.RPS;

import java.util.ArrayList;
import java.util.List;

public class RPSGameState {
    private int userWins = 0;
    private int botWins = 0;
    private int draws = 0;
    private List<String> history = new ArrayList<>();
    private boolean isExtraRound = false;

    public void addUser() {
        userWins++;
    }
    public void addBot() {
        botWins++;
    }
    public void addDraws() {
        draws++;
    }
    public int getUserWins() {
        return userWins;
    }
    public int getBotWins() {
        return botWins;
    }
    public int getDraws() {
        return draws;
    }
    public boolean getExtraRound() {
        return isExtraRound;
    }
    public void addHistory(String roundInfo) {
        history.add(roundInfo);
    }
    public int getSizeHistory() {
        return history.size();
    }
    public void changeExtraRound() {
        isExtraRound = true;
    }
}
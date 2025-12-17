package test;
import Bot.Handler.Commands.RPS.RPSGameState;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RPSGameStateTest {
    @Test
    void testScoreCounting_UserWin() {
        RPSGameState state = new RPSGameState();

        assertEquals(0, state.getUserWins());
        assertEquals(0, state.getBotWins());
        assertEquals(0, state.getDraws());

        state.addUser();

        assertEquals(1, state.getUserWins());
        assertEquals(0, state.getBotWins());
        assertEquals(0, state.getDraws());
    }

    @Test
    void testScoreCounting_BotWin() {
        RPSGameState state = new RPSGameState();

        state.addBot();
        state.addBot();

        assertEquals(0, state.getUserWins());
        assertEquals(2, state.getBotWins());
        assertEquals(0, state.getDraws());
    }

    @Test
    void testScoreCounting_Combined() {
        RPSGameState state = new RPSGameState();

        state.addUser();   // 1-0-0
        state.addUser();   // 2-0-0
        state.addBot();    // 2-1-0
        state.addDraws();  // 2-1-1
        state.addDraws();  // 2-1-2

        assertEquals(2, state.getUserWins());
        assertEquals(1, state.getBotWins());
        assertEquals(2, state.getDraws());
    }

    @Test
    void testExtraRounds() {
        RPSGameState state = new RPSGameState();
        assertFalse(state.getExtraRound());
        state.changeExtraRound();
        assertTrue(state.getExtraRound());
    }

    @Test
    void testCompleteGameScenario() {
        RPSGameState state = new RPSGameState();

        state.addUser();
        state.addHistory("Раунд 1: Вы победили");

        state.addBot();
        state.addHistory("Раунд 2: Бот победил");

        state.addDraws();
        state.addHistory("Раунд 3: Ничья");

        state.addUser();
        state.addHistory("Раунд 4: Вы победили");

        state.addDraws();
        state.addHistory("Раунд 5: Ничья");

        assertEquals(2, state.getUserWins());
        assertEquals(1, state.getBotWins());
        assertEquals(2, state.getDraws());
        assertEquals(5, state.getSizeHistory());
        assertFalse(state.getExtraRound());
    }

    @Test
    void testScoreEquality_ExtraRoundActivation() {
        RPSGameState state = new RPSGameState();

        state.addUser();
        state.addUser();
        state.addBot();
        state.addBot();
        state.addDraws();

        assertEquals(2, state.getUserWins());
        assertEquals(2, state.getBotWins());
        assertEquals(1, state.getDraws());

        state.changeExtraRound();

        assertTrue(state.getExtraRound());
    }
}

package test;
import Bot.Handler.Commands.RPS.RPSGame;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;


public class RPSGameTest {
    private RPSGame rpsGame;

    @BeforeEach
    void setUp() {
        rpsGame = new RPSGame(null);
    }
    @Test
    void testDetermineRoundResult_RockVsScissors_UserWins() {

        String result = invokeDetermineRoundResult("камень", "ножницы");
        assertEquals("user", result);
    }

    @Test
    void testDetermineRoundResult_RockVsPaper_BotWins() {
        String result = invokeDetermineRoundResult("камень", "бумага");
        assertEquals("bot", result);
    }

    @Test
    void testDetermineRoundResult_ScissorsVsPaper_UserWins() {
        String result = invokeDetermineRoundResult("ножницы", "бумага");
        assertEquals("user", result);
    }

    @Test
    void testDetermineRoundResult_ScissorsVsRock_BotWins() {
        String result = invokeDetermineRoundResult("ножницы", "камень");
        assertEquals("bot", result);
    }

    @Test
    void testDetermineRoundResult_PaperVsRock_UserWins() {
        String result = invokeDetermineRoundResult("бумага", "камень");
        assertEquals("user", result);
    }

    @Test
    void testDetermineRoundResult_PaperVsScissors_BotWins() {
        String result = invokeDetermineRoundResult("бумага", "ножницы");
        assertEquals("bot", result);
    }

    @Test
    void testDetermineRoundResult_SameChoices_Draw() {
        String[] choices = {"камень", "бумага", "ножницы"};

        for (String choice : choices) {
            String result = invokeDetermineRoundResult(choice, choice);
            assertEquals("draw", result, "Ожидалась ничья для выбора: " + choice);
        }
    }

    @Test
    void testDetermineRoundResult_InvalidChoice() {
        // При неверном выборе игрока, логика все равно будет сравнивать строки
        String result = invokeDetermineRoundResult("неправильный", "камень");
        assertEquals("bot", result);
    }
    private String invokeDetermineRoundResult(String userChoice, String botChoice) {
        try {
            var method = RPSGame.class.getDeclaredMethod("determineRoundResult", String.class, String.class);
            method.setAccessible(true);
            return (String) method.invoke(rpsGame, userChoice, botChoice);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось вызвать метод determineRoundResult", e);
        }
    }
}

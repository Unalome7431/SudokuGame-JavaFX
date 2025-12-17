package proj.sudokuGame;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SudokuModelTest {
    @Test
    public void testBoardGeneration() {
        SudokuModel model = new SudokuModel(9, 9, 3, 3);
        assertNotNull(model);
        assertEquals(0, model.getCurrentAt(0, 0));
    }

    @Test
    public void testMakeMoveValid() {
        SudokuModel model = new SudokuModel(9, 9, 3, 3);
        int answer = model.getAnswerAt(0, 0);
        MoveStatus status = model.makeMove(0, 0, answer);
        assertEquals(MoveStatus.VALID, status);
        assertEquals(answer, model.getCurrentAt(0, 0));
    }

    @Test
    public void testMakeMoveConflict() {
        SudokuModel model = new SudokuModel(9, 9, 3, 3);
        int answer = model.getAnswerAt(0, 0);
        model.makeMove(0, 0, answer);
        MoveStatus status = model.makeMove(0, 1, answer);
        assertEquals(MoveStatus.INVALID_ROW, status);
    }
}
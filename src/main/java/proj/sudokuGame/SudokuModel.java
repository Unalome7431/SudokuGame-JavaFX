package proj.sudokuGame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Represents the core logic and state of the Sudoku game.
 * <p>
 * This class handles:
 * <ul>
 * <li>Generating the board (Answer Key).</li>
 * <li>Preparing the playable board by removing clues while maintaining uniqueness.</li>
 * <li>Validating player moves.</li>
 * <li>Tracking game progress and win conditions.</li>
 * </ul>
 */
public class SudokuModel {
    /** The complete solution for the puzzle. */
    private int[][] answerKey;
    /** The current state of the board as played by the user. */
    private int[][] currentBoard;
    /** The number of currently filled cells. */
    private int filledCells = 0;
    /** The total number of rows. */
    private final int ROWS;
    /** The total number of columns. */
    private final int COLS;
    /** The width of the sub-grid. */
    private final int BOX_WIDTH;
    /** The height of the sub-grid. */
    private final int BOX_HEIGHT;

    /**
     * Constructs a new SudokuModel and generates a fresh puzzle.
     *
     * @param rows      Number of rows in the grid.
     * @param cols      Number of columns in the grid.
     * @param boxWidth  Width of the sub-grid box.
     * @param boxHeight Height of the sub-grid box.
     */
    public SudokuModel(int rows, int cols, int boxWidth, int boxHeight) {
        this.ROWS = rows;
        this.COLS = cols;
        this.BOX_WIDTH = boxWidth;
        this.BOX_HEIGHT = boxHeight;
        this.currentBoard = new int[rows][cols];

        // 1. Generate FULL Valid Board using Heuristics
        PuzzleGenerator generator = new HeuristicGenerator();
        this.answerKey = generator.generate(rows, cols, boxWidth, boxHeight);
    }

    /**
     * Prepares the board by "digging" holes (removing numbers) in the answer key.
     * <p>
     * Ensures that the resulting puzzle remains valid and has exactly one unique solution
     * by using {@link SudokuUniquenessChecker}.
     *
     * @param targetClues The target number of clues (filled cells) to remain on the board.
     */
    public void prepareBoard(int targetClues) {
        // Start with the full solution on the current board
        for (int r = 0; r < ROWS; r++) {
            System.arraycopy(answerKey[r], 0, currentBoard[r], 0, COLS);
        }

        int totalCells = ROWS * COLS;
        int cellsToRemove = totalCells - targetClues;

        // Create random order of cells to attempt removing
        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < totalCells; i++) positions.add(i);
        Collections.shuffle(positions);

        SudokuUniquenessChecker checker = new SudokuUniquenessChecker();

        for (int pos : positions) {
            if (cellsToRemove <= 0) break; // Reached target clues

            int r = pos / COLS;
            int c = pos % COLS;

            int backup = currentBoard[r][c];
            currentBoard[r][c] = 0; // Dig hole

            // CRITICAL: Check if the board still has EXACTLY 1 solution
            if (checker.hasUniqueSolution(currentBoard, ROWS, COLS, BOX_WIDTH, BOX_HEIGHT)) {
                cellsToRemove--; // Safe to remove
            } else {
                currentBoard[r][c] = backup; // Not safe, put it back
            }
        }

        // Update filledCells count for game logic
        filledCells = 0;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (currentBoard[r][c] != 0) filledCells++;
            }
        }
    }

    /**
     * Attempts to place a value in a specific cell.
     *
     * @param row   Row index.
     * @param col   Column index.
     * @param value The value to place (0 to clear the cell).
     * @return A {@link MoveStatus} indicating if the move was valid, invalid (conflict), or resulted in a win.
     */
    public MoveStatus makeMove(int row, int col, int value) {
        if (value == 0) {
            if (currentBoard[row][col] != 0) filledCells--;
            currentBoard[row][col] = 0;
            return MoveStatus.VALID;
        }

        if (isConflict(row, col, value, true, false, false)) return MoveStatus.INVALID_ROW;
        if (isConflict(row, col, value, false, true, false)) return MoveStatus.INVALID_COL;
        if (isConflict(row, col, value, false, false, true)) return MoveStatus.INVALID_BOX;

        if (currentBoard[row][col] == 0) filledCells++;
        currentBoard[row][col] = value;

        if (filledCells == (ROWS * COLS)) {
            if (checkFullBoardCorrectness()) {
                return MoveStatus.GAME_WON;
            }
        }
        return MoveStatus.VALID;
    }

    /**
     * Checks if the value at the specified coordinate matches the answer key.
     *
     * @param row Row index.
     * @param col Column index.
     * @return {@code true} if the current value matches the generated solution.
     */
    public boolean isCorrect(int row, int col) {
        return currentBoard[row][col] == answerKey[row][col];
    }

    /**
     * Gets the correct solution value for a specific cell.
     *
     * @param row Row index.
     * @param col Column index.
     * @return The correct integer value.
     */
    public int getAnswerAt(int row, int col) { return answerKey[row][col]; }

    /**
     * Gets the current value on the board for a specific cell.
     *
     * @param row Row index.
     * @param col Column index.
     * @return The integer value currently on the board (0 if empty).
     */
    public int getCurrentAt(int row, int col) { return currentBoard[row][col]; }

    /**
     * Internal helper to check for conflicts in row, column, or sub-grid.
     *
     * @param row The row index of the placed value.
     * @param col The column index of the placed value.
     * @param val The value being placed.
     * @param checkRow Whether to check for row conflicts.
     * @param checkCol Whether to check for column conflicts.
     * @param checkBox Whether to check for sub-grid conflicts.
     * @return True if a conflict exists, false otherwise.
     */
    private boolean isConflict(int row, int col, int val, boolean checkRow, boolean checkCol, boolean checkBox) {
        if (checkRow) {
            for (int c = 0; c < COLS; c++) if (c != col && currentBoard[row][c] == val) return true;
        }
        if (checkCol) {
            for (int r = 0; r < ROWS; r++) if (r != row && currentBoard[r][col] == val) return true;
        }
        if (checkBox) {
            int startRow = (row / BOX_HEIGHT) * BOX_HEIGHT;
            int startCol = (col / BOX_WIDTH) * BOX_WIDTH;
            for (int r = 0; r < BOX_HEIGHT; r++) {
                for (int c = 0; c < BOX_WIDTH; c++) {
                    int checkR = startRow + r;
                    int checkC = startCol + c;
                    if ((checkR != row || checkC != col) && currentBoard[checkR][checkC] == val) return true;
                }
            }
        }
        return false;
    }

    /**
     * Verifies if the entire board matches the answer key.
     *
     * @return True if the board is completely correct.
     */
    private boolean checkFullBoardCorrectness() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (currentBoard[r][c] != answerKey[r][c]) return false;
            }
        }
        return true;
    }
}
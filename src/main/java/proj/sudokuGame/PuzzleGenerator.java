package proj.sudokuGame;

/**
 * Interface for Sudoku Puzzle Generation.
 * <p>
 * Demonstrates Polymorphism: The game logic relies on this interface rather than
 * concrete implementations, allowing different generation algorithms (e.g., Backtracking, Heuristic)
 * to be swapped easily.
 * </p>
 */
public interface PuzzleGenerator {
    /**
     * Generates a valid, completed Sudoku board.
     *
     * @param rows      Total rows (e.g., 9 for Normal, 6 for Easy)
     * @param cols      Total cols (e.g., 9 for Normal, 6 for Easy)
     * @param boxWidth  Width of internal box (e.g., 3 for Normal, 3 for Easy)
     * @param boxHeight Height of internal box (e.g., 3 for Normal, 2 for Easy)
     * @return A 2D array representing the generated answer key.
     */
    int[][] generate(int rows, int cols, int boxWidth, int boxHeight);
}
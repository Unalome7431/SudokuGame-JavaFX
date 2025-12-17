package proj.sudokuGame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generates a full Sudoku board using MRV (Minimum Remaining Values) Heuristics.
 * <p>
 * This generator is generally faster than pure backtracking because it prioritizes filling
 * cells that have the fewest possible valid options, reducing the search tree size.
 */
public class HeuristicGenerator implements PuzzleGenerator {

    /**
     * Default constructor.
     */
    public HeuristicGenerator() {
    }

    /**
     * Generates a valid, completed Sudoku board.
     *
     * @param rows      Total rows in the grid.
     * @param cols      Total columns in the grid.
     * @param boxWidth  Width of the sub-grid.
     * @param boxHeight Height of the sub-grid.
     * @return A 2D array representing the generated solution.
     */
    @Override
    public int[][] generate(int rows, int cols, int boxWidth, int boxHeight) {
        int[][] board = new int[rows][cols];

        // 1. Fill diagonal boxes independently to ensure randomness
        // (Diagonal boxes don't affect each other initially)
        fillDiagonalBoxes(board, rows, cols, boxWidth, boxHeight);

        // 2. Solve the rest using MRV Heuristics to get a complete board
        solveSudokuWithHeuristics(board, rows, cols, boxWidth, boxHeight);

        return board;
    }

    /**
     * Fills the diagonal sub-grids with random numbers.
     * Since diagonal boxes are independent of each other, this can be done safely before solving.
     *
     * @param board The game board.
     * @param rows Total rows.
     * @param cols Total columns.
     * @param boxWidth Box width.
     * @param boxHeight Box height.
     */
    private void fillDiagonalBoxes(int[][] board, int rows, int cols, int boxWidth, int boxHeight) {
        int numBoxes = Math.min(rows / boxHeight, cols / boxWidth);
        for (int i = 0; i < numBoxes; i++) {
            fillBox(board, i * boxHeight, i * boxWidth, rows, boxWidth, boxHeight);
        }
    }

    /**
     * Fills a specific sub-grid box with random numbers.
     *
     * @param board The game board.
     * @param startRow Row to start filling.
     * @param startCol Column to start filling.
     * @param maxNum Maximum number value.
     * @param boxWidth Box width.
     * @param boxHeight Box height.
     */
    private void fillBox(int[][] board, int startRow, int startCol, int maxNum, int boxWidth, int boxHeight) {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= maxNum; i++) numbers.add(i);
        Collections.shuffle(numbers);

        int index = 0;
        for (int r = 0; r < boxHeight; r++) {
            for (int c = 0; c < boxWidth; c++) {
                if (index < numbers.size()) {
                    board[startRow + r][startCol + c] = numbers.get(index++);
                }
            }
        }
    }

    // --- MRV Logic ---

    /**
     * Solves the Sudoku board using Minimum Remaining Values (MRV) heuristic.
     *
     * @param sudoku The board to solve.
     * @param rows Total rows.
     * @param cols Total columns.
     * @param boxWidth Box width.
     * @param boxHeight Box height.
     * @return {@code true} if a solution is found.
     */
    private boolean solveSudokuWithHeuristics(int[][] sudoku, int rows, int cols, int boxWidth, int boxHeight) {
        int[] bestCell = findBestCell(sudoku, rows, cols, boxWidth, boxHeight);
        int row = bestCell[0];
        int col = bestCell[1];

        if (row == -1 || col == -1) return true; // Solved

        for (int num = 1; num <= rows; num++) {
            if (isValidOption(sudoku, num, row, col, rows, boxWidth, boxHeight)) {
                sudoku[row][col] = num;
                if (solveSudokuWithHeuristics(sudoku, rows, cols, boxWidth, boxHeight)) {
                    return true;
                }
                sudoku[row][col] = 0; // Backtrack
            }
        }
        return false;
    }

    /**
     * Finds the empty cell with the fewest possible valid moves (MRV).
     *
     * @param sudoku The board.
     * @param rows Total rows.
     * @param cols Total columns.
     * @param boxWidth Box width.
     * @param boxHeight Box height.
     * @return An array containing {row, col} of the best cell to try next.
     */
    private int[] findBestCell(int[][] sudoku, int rows, int cols, int boxWidth, int boxHeight) {
        int bestRow = -1;
        int bestCol = -1;
        int minOptions = rows + 1;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (sudoku[r][c] == 0) {
                    int possibleOptions = countValidOption(sudoku, r, c, rows, boxWidth, boxHeight);
                    if (possibleOptions < minOptions) {
                        minOptions = possibleOptions;
                        bestRow = r;
                        bestCol = c;
                    }
                    if (possibleOptions <= 1) return new int[] {bestRow, bestCol};
                }
            }
        }
        return new int[] {bestRow, bestCol};
    }

    /**
     * Counts how many valid numbers can be placed in a specific cell.
     *
     * @param sudoku The board.
     * @param row Row index.
     * @param col Column index.
     * @param maxNum Max number.
     * @param boxWidth Box width.
     * @param boxHeight Box height.
     * @return Count of valid options.
     */
    private int countValidOption(int[][] sudoku, int row, int col, int maxNum, int boxWidth, int boxHeight) {
        int validCounter = 0;
        for (int i = 1; i <= maxNum; i++) {
            if (isValidOption(sudoku, i, row, col, maxNum, boxWidth, boxHeight)) validCounter++;
        }
        return validCounter;
    }

    /**
     * Checks if a number is valid at a given position.
     *
     * @param sudoku The board.
     * @param num The number to check.
     * @param row Row index.
     * @param col Column index.
     * @param maxNum Max number.
     * @param boxWidth Box width.
     * @param boxHeight Box height.
     * @return True if valid.
     */
    private boolean isValidOption(int[][] sudoku, int num, int row, int col, int maxNum, int boxWidth, int boxHeight) {
        int localBoxRow = row - (row % boxHeight);
        int localBoxCol = col - (col % boxWidth);

        for (int r = localBoxRow; r < localBoxRow + boxHeight; r++) {
            for (int c = localBoxCol; c < localBoxCol + boxWidth; c++) {
                if (sudoku[r][c] == num) return false;
            }
        }
        for (int i = 0; i < maxNum; i++) {
            if (sudoku[row][i] == num || sudoku[i][col] == num) return false;
        }
        return true;
    }
}
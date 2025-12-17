package proj.sudokuGame;

/**
 * Utility class to check if a Sudoku puzzle has exactly one unique solution.
 * <p>
 * This is crucial during puzzle generation to ensuring the puzzle is valid and solvable logic
 * rather than guessing.
 */
public class SudokuUniquenessChecker {

    /** Counter for valid solutions found. */
    private int solutionsFound = 0;

    /**
     * Default constructor.
     */
    public SudokuUniquenessChecker() {
    }

    /**
     * Checks if the given puzzle configuration results in exactly one unique solution.
     *
     * @param puzzleBoard The board state to check (0 represents empty cells).
     * @param rows        Total rows.
     * @param cols        Total columns.
     * @param boxWidth    Sub-grid width.
     * @param boxHeight   Sub-grid height.
     * @return {@code true} if exactly one solution exists, {@code false} otherwise.
     */
    public boolean hasUniqueSolution(int[][] puzzleBoard, int rows, int cols, int boxWidth, int boxHeight) {
        solutionsFound = 0;

        // Clone the board to avoid modifying the game state
        int[][] boardCopy = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(puzzleBoard[i], 0, boardCopy[i], 0, cols);
        }

        solveAndCount(boardCopy, rows, cols, boxWidth, boxHeight);

        return solutionsFound == 1;
    }

    /**
     * Recursively solves the board and counts the number of valid solutions found.
     * Optimizes by stopping if more than 1 solution is found.
     *
     * @param board The game board.
     * @param rows Total rows.
     * @param cols Total columns.
     * @param boxWidth Box width.
     * @param boxHeight Box height.
     */
    private void solveAndCount(int[][] board, int rows, int cols, int boxWidth, int boxHeight) {
        if (solutionsFound > 1) return; // Optimization: Stop if not unique

        int[] empty = findEmpty(board, rows, cols);
        int row = empty[0];
        int col = empty[1];

        if (row == -1) {
            solutionsFound++;
            return;
        }

        for (int num = 1; num <= rows; num++) {
            if (isValid(board, num, row, col, rows, boxWidth, boxHeight)) {
                board[row][col] = num;
                solveAndCount(board, rows, cols, boxWidth, boxHeight);
                board[row][col] = 0;
            }
        }
    }

    /**
     * Validates if a number placement does not violate Sudoku rules.
     *
     * @param board The board.
     * @param num The number to check.
     * @param row Row index.
     * @param col Column index.
     * @param maxNum Max number value.
     * @param boxWidth Box width.
     * @param boxHeight Box height.
     * @return True if valid.
     */
    private boolean isValid(int[][] board, int num, int row, int col, int maxNum, int boxWidth, int boxHeight) {
        for (int i = 0; i < maxNum; i++) if (board[row][i] == num) return false;
        for (int i = 0; i < maxNum; i++) if (board[i][col] == num) return false;

        int boxRowStart = row - (row % boxHeight);
        int boxColStart = col - (col % boxWidth);
        for (int r = boxRowStart; r < boxRowStart + boxHeight; r++) {
            for (int c = boxColStart; c < boxColStart + boxWidth; c++) {
                if (board[r][c] == num) return false;
            }
        }
        return true;
    }

    /**
     * Finds the next empty cell on the board.
     *
     * @param board The board.
     * @param rows Total rows.
     * @param cols Total columns.
     * @return {row, col} or {-1, -1} if full.
     */
    private int[] findEmpty(int[][] board, int rows, int cols) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (board[r][c] == 0) return new int[]{r, c};
            }
        }
        return new int[]{-1, -1};
    }
}
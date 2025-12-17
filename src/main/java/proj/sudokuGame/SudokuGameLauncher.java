package proj.sudokuGame;

/**
 * Launcher class for the Sudoku Game.
 * <p>
 * This class is necessary to properly launch JavaFX applications from a JAR file
 * or certain IDEs that require the main class to not extend {@code Application}.
 */
public class SudokuGameLauncher {

    /**
     * Default constructor.
     */
    public SudokuGameLauncher() {
    }

    /**
     * Main entry point of the application.
     * Delegates to {@link SudokuGameMain#main(String[])}.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        SudokuGameMain.main(args);
    }
}
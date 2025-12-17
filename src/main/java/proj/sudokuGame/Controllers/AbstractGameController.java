package proj.sudokuGame.Controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import proj.sudokuGame.MoveStatus;
import proj.sudokuGame.SudokuModel;

import java.io.IOException;
import java.util.Random;
import java.util.function.UnaryOperator;

/**
 * Abstract Base Controller that handles the core logic for any Sudoku game mode.
 * <p>
 * This class serves as the bridge between the UI (FXML) and the Data (SudokuModel).
 * It manages:
 * <ul>
 * <li>The game timer loop.</li>
 * <li>Input validation for the Sudoku grid (numbers 1-9 only).</li>
 * <li>Delegating moves to the Model.</li>
 * <li>Visual feedback for valid/invalid moves.</li>
 * <li>Navigation to Home or Winning screens.</li>
 * </ul>
 */
public abstract class AbstractGameController {

    /** The main grid container from the FXML file. */
    @FXML protected GridPane boardGrid;

    /** The label displaying the elapsed time. */
    @FXML protected Label timeLabel;

    /** The logical model representing the game state (board, rules, validation). */
    protected SudokuModel model;

    /** A 2D array mapping the visual TextFields to [row][col] coordinates. */
    protected TextField[][] textFields;

    /** Flag to control the background timer thread. */
    private boolean isGameRunning = true;

    /** Tracks the total seconds played. */
    private long secondsElapsed = 0;

    /**
     * Default constructor.
     */
    public AbstractGameController() {
    }

    // --- Abstract Configuration Methods ---

    /**
     * Gets the row count.
     * @return The number of rows in this specific game mode (e.g., 9 or 6).
     */
    protected abstract int getRows();

    /**
     * Gets the column count.
     * @return The number of columns in this specific game mode.
     */
    protected abstract int getCols();

    /**
     * Gets the box width.
     * @return The width of a sub-grid box (e.g., 3).
     */
    protected abstract int getBoxWidth();

    /**
     * Gets the box height.
     * @return The height of a sub-grid box (e.g., 3 or 2).
     */
    protected abstract int getBoxHeight();

    /**
     * Gets the initial clues count.
     * @return The number of clues (filled numbers) generated at the start.
     */
    protected abstract int getInitialClues();

    /**
     * Parses the FXML GridPane structure and maps it to the {@code textFields} array.
     * This is necessary because different FXML layouts might nest GridPanes differently.
     */
    protected abstract void mapBoard();

    /**
     * Gets the resource path of the FXML file for the current mode.
     * Used to restart the specific game mode from the Winning Screen.
     *
     * @return A string path (e.g., "/proj/sudokuGame/normalBoard_3x3.fxml").
     */
    protected abstract String getGameModeFxmlPath();

    /**
     * JavaFX initialization method. Called automatically after the FXML file is loaded.
     * <p>
     * Initializes the Model, maps the board, sets up the UI with clues, and starts the timer.
     */
    @FXML
    public void initialize() {
        this.textFields = new TextField[getRows()][getCols()];
        this.model = new SudokuModel(getRows(), getCols(), getBoxWidth(), getBoxHeight());

        // mapBoard implementation in subclasses handles linking UI to Array
        mapBoard();

        model.prepareBoard(getInitialClues());
        updateBoardFromModel();
        startTimer();
    }

    /**
     * Configures a single TextField cell.
     * <p>
     * Adds a TextFormatter to ensure only numbers 1-9 are entered.
     * Adds a Listener to trigger game logic whenever the text changes.
     *
     * @param tf The JavaFX TextField component.
     * @param r  The row index logic.
     * @param c  The column index logic.
     */
    protected void setupTextField(TextField tf, int r, int c) {
        textFields[r][c] = tf;

        // Regex filter: Only allow digits 1-9
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[1-9]?")) {
                return change;
            }
            return null;
        };
        tf.setTextFormatter(new TextFormatter<>(filter));

        // Listener for player moves
        tf.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) {
                int val = Integer.parseInt(newVal);
                MoveStatus status = model.makeMove(r, c, val);
                handleMoveStatus(status, tf);
            } else {
                model.makeMove(r, c, 0); // Player cleared cell
                tf.getStyleClass().removeAll("wrong-input");
            }
        });
    }

    /**
     * Visually updates the cell based on whether the move was valid or invalid.
     *
     * @param status The result of the move from the Model.
     * @param tf     The TextField to update.
     */
    private void handleMoveStatus(MoveStatus status, TextField tf) {
        tf.getStyleClass().remove("wrong-input");
        switch (status) {
            case INVALID_ROW:
            case INVALID_COL:
            case INVALID_BOX:
                // Add red styling if not already present
                if (!tf.getStyleClass().contains("wrong-input")) {
                    tf.getStyleClass().add("wrong-input");
                }
                break;
            case GAME_WON:
                isGameRunning = false;
                showWinningScreen();
                break;
        }
    }

    /**
     * Handles the "Check" / "Solve" button click.
     * <p>
     * Iterates through the board:
     * 1. Marks incorrect inputs in red.
     * 2. Locks and marks correct inputs in green.
     *
     * @param e The ActionEvent.
     */
    @FXML
    protected void onCheck(ActionEvent e) {
        for (int r = 0; r < getRows(); r++) {
            for (int c = 0; c < getCols(); c++) {
                TextField tf = textFields[r][c];
                String text = tf.getText();
                if (!text.isEmpty()) {
                    if (!model.isCorrect(r, c)) {
                        if (!tf.getStyleClass().contains("wrong-input")) {
                            tf.getStyleClass().add("wrong-input");
                        }
                    } else {
                        // Mark correct user inputs
                        if (!tf.getStyleClass().contains("on-filled") && !tf.getStyleClass().contains("right-input")) {
                            tf.getStyleClass().add("right-input");
                            tf.setDisable(true);
                        }
                    }
                }
            }
        }
    }

    /**
     * Handles the "Hint" button click.
     * <p>
     * Randomly finds an empty cell and fills it with the correct answer.
     * Limits attempts to avoid infinite loops if the board is nearly full.
     *
     * @param e The ActionEvent.
     */
    @FXML
    protected void onHint(ActionEvent e) {
        Random rand = new Random();
        int attempts = 0;
        while (attempts < 3) { // Try finding an empty spot 3 times
            int r = rand.nextInt(getRows());
            int c = rand.nextInt(getCols());
            if (model.getCurrentAt(r, c) == 0) {
                int correctVal = model.getAnswerAt(r, c);
                model.makeMove(r, c, correctVal);
                TextField tf = textFields[r][c];
                tf.setText(String.valueOf(correctVal));
                tf.setDisable(true);
                tf.getStyleClass().add("on-filled");
                return;
            }
            attempts++;
        }
    }

    /**
     * Handles the "Home" button click.
     * <p>
     * Pauses the timer and opens a Confirmation Popup.
     * If the user cancels, the timer resumes.
     *
     * @param e The ActionEvent.
     */
    @FXML
    protected void onHome(ActionEvent e) {
        try {
            isGameRunning = false; // Pause the timer loop

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/proj/sudokuGame/confirmation.fxml"));
            Parent root = loader.load();

            ConfirmationController controller = loader.getController();

            Node source = (Node) e.getSource();
            Stage game = (Stage) source.getScene().getWindow();

            // Set Confirm Action: Close current game window
            controller.setOnConfirm(() -> {
                game.close();
            });

            // Set Cancel Action: Resume the timer
            Runnable resumeAction = () -> {
                if (!isGameRunning) {
                    isGameRunning = true;
                    startTimer();
                }
            };
            controller.setOnCancel(resumeAction);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            // Ensure timer resumes if user closes popup via "X" button
            stage.setOnCloseRequest(event -> resumeAction.run());

            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Syncs the UI with the initial state of the Model (filling the clues).
     */
    private void updateBoardFromModel() {
        for (int r = 0; r < getRows(); r++) {
            for (int c = 0; c < getCols(); c++) {
                int val = model.getCurrentAt(r, c);
                if (val != 0) {
                    TextField tf = textFields[r][c];
                    tf.setText(String.valueOf(val));
                    tf.setDisable(true); // Clues cannot be edited
                    tf.getStyleClass().add("on-filled"); // Styling for clues
                }
            }
        }
    }

    /**
     * Starts a background daemon thread to count seconds.
     * Updates the UI Label on the JavaFX Application Thread.
     */
    private void startTimer() {
        Thread timerThread = new Thread(() -> {
            while (isGameRunning) {
                try {
                    Thread.sleep(1000);
                    secondsElapsed++;
                    String timeText = String.format("Time: %02d:%02d", (secondsElapsed % 3600) / 60, secondsElapsed % 60);
                    Platform.runLater(() -> {
                        if (timeLabel != null) timeLabel.setText(timeText);
                    });
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        timerThread.setDaemon(true); // Ensures thread dies when app closes
        timerThread.start();
    }

    /**
     * Loads and displays the Game Winning screen.
     * Passes the final time and the current game mode (for Play Again) to the next controller.
     */
    private void showWinningScreen() {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/proj/sudokuGame/gameWinning.fxml"));
                Parent winRoot = loader.load();

                GameWinningController controller = loader.getController();
                controller.setFinalTime(timeLabel.getText());
                controller.setPreviousMode(getGameModeFxmlPath()); // Pass path for restart

                Stage stage = (Stage) boardGrid.getScene().getWindow();
                stage.setScene(new Scene(winRoot));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
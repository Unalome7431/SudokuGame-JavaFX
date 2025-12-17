package proj.sudokuGame.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Controller for the "Game Won" screen.
 * <p>
 * This screen is displayed when the user successfully solves the puzzle.
 * It shows the final time and allows restarting the same mode or returning to the menu.
 */
public class GameWinningController {

    /** Label to show the finished time. */
    @FXML private Label timeLabel;

    /** Stores the FXML path of the mode that was just finished (e.g., Easy or Normal). */
    private String previousFxml;

    /**
     * Default constructor.
     */
    public GameWinningController() {
    }

    /**
     * Updates the UI to show the player's final time.
     *
     * @param time The formatted time string (e.g., "Time: 05:30").
     */
    public void setFinalTime(String time) {
        timeLabel.setText("You finished in: " + time.replace("Time: ", ""));
    }

    /**
     * Sets the mode that was just played.
     * This allows the "Play Again" button to know which FXML to load.
     *
     * @param fxmlPath The path to the FXML file (e.g., "/proj/sudokuGame/easyBoard_2x3.fxml").
     */
    public void setPreviousMode(String fxmlPath) {
        this.previousFxml = fxmlPath;
    }

    /**
     * Handles the "Play Again" button.
     * Restarts the game using the {@code previousFxml} path.
     *
     * @param e The ActionEvent.
     */
    @FXML
    public void onPlayAgain(ActionEvent e) {
        if (previousFxml == null) {
            onHome(e); // Safety fallback
            return;
        }

        Node source = (Node) e.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();

        try {
            Stage gameStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(previousFxml));
            Parent root = loader.load();

            gameStage.setScene(new Scene(root));
            gameStage.setResizable(false);

            // Simple logic to set the window title based on the file path
            String title = previousFxml.contains("easy") ? "Sudoku Game: Easy Mode" : "Sudoku Game: Normal Mode";
            gameStage.setTitle(title);

            gameStage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Handles the "Home" button.
     * Returns the user to the main menu.
     *
     * @param e The ActionEvent.
     */
    @FXML
    public void onHome(ActionEvent e) {
        Node source = (Node) e.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
        try {
            Stage homeStage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/proj/sudokuGame/home.fxml"));
            homeStage.setScene(new Scene(root));
            homeStage.setResizable(false);
            homeStage.setTitle("Sudoku Game");
            homeStage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
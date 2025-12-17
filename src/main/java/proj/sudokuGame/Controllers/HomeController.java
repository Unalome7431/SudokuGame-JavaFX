package proj.sudokuGame.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Controller for the Main Menu (Home Screen).
 * <p>
 * Handles navigation to different game modes (Easy, Normal) and exiting the application.
 */
public class HomeController {

    /**
     * Default constructor.
     */
    public HomeController() {
    }

    /**
     * Launches the Easy Mode game.
     * @param e The action event.
     */
    @FXML
    public void playEasyMode(ActionEvent e) {
        launchGame(e, "/proj/sudokuGame/easyBoard_2x3.fxml", "Sudoku: Easy");
    }

    /**
     * Launches the Normal Mode game.
     * @param e The action event.
     */
    @FXML
    public void playNormalMode(ActionEvent e) {
        launchGame(e, "/proj/sudokuGame/normalBoard_3x3.fxml", "Sudoku: Normal");
    }

    /**
     * Helper method to load a game scene.
     *
     * @param e         The action event (used to close current window).
     * @param fxmlPath  Path to the FXML file.
     * @param title     Title for the new window.
     */
    private void launchGame(ActionEvent e, String fxmlPath, String title) {
        Node source = (Node) e.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        currentStage.close();
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.setTitle(title);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Exits the application.
     * @param e The action event.
     */
    @FXML
    public void exitGame(ActionEvent e) {
        Node source = (Node) e.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        currentStage.close();
    }
}
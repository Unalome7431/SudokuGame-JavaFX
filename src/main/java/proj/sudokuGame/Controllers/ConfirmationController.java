package proj.sudokuGame.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;
import proj.sudokuGame.SudokuGameMain;

import java.io.IOException;

/**
 * Controller for the Confirmation Dialog Popup.
 * <p>
 * This controller handles the user's decision when they attempt to exit a running game.
 * It supports callback actions for both confirming the exit and cancelling (resuming the game).
 */
public class ConfirmationController {

    /** Action to execute if confirmed. */
    private Runnable onConfirmAction;
    /** Action to execute if cancelled. */
    private Runnable onCancelAction;

    /**
     * Default constructor.
     */
    public ConfirmationController() {
    }

    /**
     * Registers a callback to be executed when the user clicks "Yes".
     * Usually used to close the game window.
     *
     * @param action The Runnable action.
     */
    public void setOnConfirm(Runnable action) {
        this.onConfirmAction = action;
    }

    /**
     * Registers a callback to be executed when the user clicks "No".
     * Usually used to resume the paused game timer.
     *
     * @param action The Runnable action.
     */
    public void setOnCancel(Runnable action) {
        this.onCancelAction = action;
    }

    /**
     * Handles the "Yes" button.
     * Executes the confirm action, closes the popup, and returns to the Home Screen.
     *
     * @param e The ActionEvent.
     */
    @FXML
    public void backHome(ActionEvent e){
        if (onConfirmAction != null){
            onConfirmAction.run();
        }

        // Close the confirmation window
        Node source = (Node)e.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();

        // Launch the Home Screen
        try {
            Stage homeStage = new Stage();
            SudokuGameMain sudokuGameMain = new SudokuGameMain();
            sudokuGameMain.start(homeStage);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Handles the "No" button.
     * Executes the cancel action (resume timer) and closes the popup.
     *
     * @param e The ActionEvent.
     */
    @FXML
    public void backGame(ActionEvent e){
        if (onCancelAction != null) {
            onCancelAction.run();
        }

        Node source = (Node)e.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
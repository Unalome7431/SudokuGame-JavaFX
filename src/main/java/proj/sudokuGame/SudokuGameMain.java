package proj.sudokuGame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The main JavaFX Application class.
 * <p>
 * Handles the initialization of the primary stage and loading of the initial Home View.
 */
public class SudokuGameMain extends Application {

    /**
     * Default constructor.
     */
    public SudokuGameMain() {
    }

    /**
     * Starts the JavaFX application.
     * Loads the {@code home.fxml} file and sets up the primary window.
     *
     * @param primaryStage The primary stage for this application.
     * @throws Exception If the FXML resource cannot be loaded.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/proj/sudokuGame/home.fxml"));

        Parent root = loader.load();

        Scene scene = new Scene(root);

        primaryStage.setTitle("Sudoku Game");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be launched through deployment artifacts,
     * e.g., in IDEs with limited FX support.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
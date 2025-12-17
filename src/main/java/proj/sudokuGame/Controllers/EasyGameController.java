package proj.sudokuGame.Controllers;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Controller specifically for the Easy Mode (6x6 Board).
 * <p>
 * Defines the dimensions and mapping logic for the 2x3 sub-grids found in a 6x6 Sudoku.
 */
public class EasyGameController extends AbstractGameController {

    /**
     * Default constructor.
     */
    public EasyGameController() {
    }

    @Override protected int getRows() { return 6; }
    @Override protected int getCols() { return 6; }
    @Override protected int getBoxWidth() { return 3; }
    @Override protected int getBoxHeight() { return 2; }
    @Override protected int getInitialClues() { return 15; }

    /**
     * Returns the FXML file path for Easy Mode.
     * Used by the Winning Screen to restart the correct game mode.
     *
     * @return The path string.
     */
    @Override
    protected String getGameModeFxmlPath() {
        return "/proj/sudokuGame/easyBoard_2x3.fxml";
    }

    /**
     * Maps the nested GridPane structure of the 6x6 board to the logical 2D array.
     */
    @Override
    protected void mapBoard() {
        // Inherits boardGrid from AbstractGameController
        for (Node outerChild : boardGrid.getChildren()) {
            if (outerChild instanceof GridPane) {
                GridPane innerGrid = (GridPane) outerChild;
                Integer outerRow = GridPane.getRowIndex(innerGrid);
                Integer outerCol = GridPane.getColumnIndex(innerGrid);
                if (outerRow == null) outerRow = 0;
                if (outerCol == null) outerCol = 0;

                int startRow = outerRow * getBoxHeight();
                int startCol = outerCol * getBoxWidth();

                for (Node innerChild : innerGrid.getChildren()) {
                    if (innerChild instanceof TextField) {
                        Integer innerRow = GridPane.getRowIndex(innerChild);
                        Integer innerCol = GridPane.getColumnIndex(innerChild);
                        if (innerRow == null) innerRow = 0;
                        if (innerCol == null) innerCol = 0;

                        setupTextField((TextField) innerChild, startRow + innerRow, startCol + innerCol);
                    }
                }
            }
        }
    }
}
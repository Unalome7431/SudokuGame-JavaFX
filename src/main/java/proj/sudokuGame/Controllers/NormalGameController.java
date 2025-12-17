package proj.sudokuGame.Controllers;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Controller specifically for the Normal Mode (9x9 Board).
 * <p>
 * Defines the dimensions and mapping logic for the 3x3 sub-grids found in a standard 9x9 Sudoku.
 */
public class NormalGameController extends AbstractGameController {

    /**
     * Default constructor.
     */
    public NormalGameController() {
    }

    @Override protected int getRows() { return 9; }
    @Override protected int getCols() { return 9; }
    @Override protected int getBoxWidth() { return 3; }
    @Override protected int getBoxHeight() { return 3; }
    @Override protected int getInitialClues() { return 30; }

    /**
     * Returns the FXML file path for Normal Mode.
     * Used by the Winning Screen to restart the correct game mode.
     *
     * @return The path string.
     */
    @Override
    protected String getGameModeFxmlPath() {
        return "/proj/sudokuGame/normalBoard_3x3.fxml";
    }

    /**
     * Maps the nested GridPane structure of the 9x9 board to the logical 2D array.
     */
    @Override
    protected void mapBoard() {
        for (Node outerChild : boardGrid.getChildren()) {
            if (outerChild instanceof GridPane) {
                GridPane innerGrid = (GridPane) outerChild;
                Integer outerRow = GridPane.getRowIndex(innerGrid);
                Integer outerCol = GridPane.getColumnIndex(innerGrid);
                if (outerRow == null) outerRow = 0;
                if (outerCol == null) outerCol = 0;

                int startRow = outerRow * 3;
                int startCol = outerCol * 3;

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
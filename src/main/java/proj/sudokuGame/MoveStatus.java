package proj.sudokuGame;

/**
 * Defines the possible outcomes of a player's move.
 * Used to communicate the result of an action between the Model and the Controller.
 */
public enum MoveStatus {
    /** The move follows the rules (no conflict with row, column, or box). */
    VALID,

    /** The move causes a conflict with another number in the same row. */
    INVALID_ROW,

    /** The move causes a conflict with another number in the same column. */
    INVALID_COL,

    /** The move causes a conflict with another number in the same sub-grid (box). */
    INVALID_BOX,

    /** The move completes the board correctly, resulting in a win. */
    GAME_WON
}
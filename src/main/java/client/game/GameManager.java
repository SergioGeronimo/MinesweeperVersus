package client.game;

import model.board.Board;
import model.board.Box;
import model.board.BoxStatus;
import model.board.BoxValue;

public class GameManager {

    //static ConectionManager conectionManager;

    private Board playerBoard, rivalBoard;
    private int columns, rows, mines;

    public GameManager(int columns, int rows, int mines) {
        this.columns = columns;
        this.rows = rows;
        this.mines = mines;
    }

    public void setMatchReady() {
        playerBoard = new Board(columns,rows,mines);
        rivalBoard = new Board(columns,rows, mines);
        playerBoard.generateEmptyGrid();
        rivalBoard.generateEmptyGrid();
    }

    public Board getPlayerBoard() {
        return playerBoard;
    }

    public void setPlayerBoard(Board playerBoard) {
        this.playerBoard = playerBoard;
    }

    public Board getRivalBoard() {
        return rivalBoard;
    }

    public void setRivalBoard(Board rivalBoard) {
        this.rivalBoard = rivalBoard;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getMines() {
        return mines;
    }

    public void setMines(int mines) {
        this.mines = mines;
    }

    public void startMatchAt(int column, int row) {
        playerBoard.generateMines(column, row);
        playerBoard.fillNearIndicators();
    }

    public BoxValue revealPlayerBoxValue(int column, int row) {
        playerBoard.setBoxStatus(column, row, BoxStatus.VISIBLE);

        return playerBoard.getBoxAt(column, row).getValue();
    }

    public void toggleFlagStatus(int column, int row){
        Box box = playerBoard.getBoxAt(column, row);

        switch (box.getStatus()){
            case HIDDEN:
                box.setStatus(BoxStatus.FLAGGED);
                break;
            case FLAGGED:
                box.setStatus(BoxStatus.HIDDEN);
                break;
        }
    }
}

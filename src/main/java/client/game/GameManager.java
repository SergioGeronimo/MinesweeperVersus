package client.game;

import model.board.Board;

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
        playerBoard = new Board(10,10,10);
        rivalBoard = new Board(10,10,10);
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
    }
}

package client.game;

import model.board.Board;
import model.board.Box;
import model.board.BoxStatus;
import model.board.BoxValue;

public class GameManager {

    //static ConnectionManager connectionManager;

    private Board playerBoard, rivalBoard;
    private int columns, rows, mines;
    private int playerCorrectFlags = 0;
    private GameState gameState = GameState.UNDEFINED;



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
        //playerBoard.setBoxStatus(column, row, BoxStatus.VISIBLE);

        try {
            playerBoard.setVisibleEmptyNeighbours(column, row);
        }catch (ArrayIndexOutOfBoundsException ignored){

        }

        calculateGameState(column, row, false);

        return playerBoard.getBoxAt(column, row).getValue();
    }

    private void calculateGameState(int column, int row, boolean playerDroppedFlag) {
        Box box = playerBoard.getBoxAt(column, row);


        if (box.getValue() == BoxValue.MINE && box.getStatus() == BoxStatus.VISIBLE){
            this.gameState = GameState.PLAYER_LOST_BY_MINE;

        }else if (box.getValue() == BoxValue.MINE && box.getStatus() == BoxStatus.FLAGGED){
            playerCorrectFlags++;

            if (playerCorrectFlags == mines){
                this.gameState = GameState.PLAYER_WON_BY_FLAG;
            }

        }else if(playerDroppedFlag && box.getValue() == BoxValue.MINE){
            playerCorrectFlags--;
        }
        /*
        if(gameState != GameState.UNDEFINED){
            notifyGameState();
        }*/
        System.out.println("GameState = " + gameState);

    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void toggleFlagStatus(int column, int row){
        Box box = playerBoard.getBoxAt(column, row);

        switch (box.getStatus()){
            case HIDDEN:
                box.setStatus(BoxStatus.FLAGGED);
                calculateGameState(column, row, false);
                break;
            case FLAGGED:
                box.setStatus(BoxStatus.HIDDEN);
                calculateGameState(column, row, true);
                break;
        }
    }
}

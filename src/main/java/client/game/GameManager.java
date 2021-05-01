package client.game;

import model.board.Board;
import model.board.Box;
import model.board.BoxStatus;
import model.board.BoxValue;

public class GameManager {



    private Board playerBoard, rivalBoard;
    private int columns, rows, mines;
    private int playerCorrectFlags = 0;
    private GameState gameState = GameState.UNDEFINED;



    public GameManager(int columns, int rows, int mines) {
        this.columns = columns;
        this.rows = rows;
        this.mines = mines;
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

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }


    public void setMatchReady() {
        playerBoard = new Board(columns,rows,mines);
        rivalBoard = new Board(columns,rows, mines);
        playerBoard.generateEmptyGrid();
        rivalBoard.generateEmptyGrid();
    }

    //genera minas y los indicadores
    public void startMatchAt(int column, int row) {
        playerBoard.generateMines(column, row);
        playerBoard.fillNearIndicators();
    }


    public void revealPlayerBoxValue(int column, int row) {
        try {
            playerBoard.setVisibleEmptyNeighbours(column, row);
        }catch (ArrayIndexOutOfBoundsException ignored){

        }
        playerBoard.printBoard();
        calculateGameState(column, row, false);

    }


    /*  calcula el estado del juego con una casilla
     *  @param coordenadas de la casilla(column, row) y playerDroppedFlag si en esa casilla se quito una bandera
     */
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
        playerBoard.printBoard();
    }
}

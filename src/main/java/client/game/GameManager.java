package client.game;

import client.connection.ClientConnection;
import client.model.*;
import javafx.concurrent.Task;

import java.rmi.RemoteException;

public class GameManager implements Runnable{

    /*Clase exclusiva para administrar las redireccion
    * a la logica del juego o la conexion con los datos
    *
    *
    * */
    Thread updateInfoThread;
    Player player, rival;
    private boolean isPlayerA;
    private int matchID;
    private Board playerBoard, rivalBoard;
    private int columns, rows, mines;
    private int playerCorrectFlags = 0;
    private GameState gameState = GameState.UNDEFINED;



    public GameManager(int columns, int rows, int mines) {
        this.columns = columns;
        this.rows = rows;
        this.mines = mines;

        updateInfoThread = new Thread(this);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getRival() {
        return rival;
    }

    public void setRival(Player rival) {
        this.rival = rival;
    }

    public int getMatchID() {
        return matchID;
    }

    public void setMatchID(int matchID) {
        this.matchID = matchID;
    }

    public int getPlayerCorrectFlags() {
        return playerCorrectFlags;
    }

    public void setPlayerCorrectFlags(int playerCorrectFlags) {
        this.playerCorrectFlags = playerCorrectFlags;
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
        updateInfoThread.start();
        playerBoard = new Board(columns,rows,mines);
        playerBoard.generateEmptyGrid();
        try {
            isPlayerA = ClientConnection.askIsPlayerA(this.matchID, player.getNickname());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    //genera minas y los indicadores
    public void startMatchAt(int column, int row) {
        playerBoard.generateMines(column, row);
        playerBoard.fillNearIndicators();

        try {
            ClientConnection.sendBoard(matchID, isPlayerA, playerBoard) ;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    //descubrir la casilla y enviarla
    public void revealPlayerBoxValue(int column, int row) {
        try {
            playerBoard.setVisibleEmptyNeighbours(column, row);
            ClientConnection.sendBox(matchID, isPlayerA, playerBoard.getBoxAt(column, row));
        }catch (ArrayIndexOutOfBoundsException ignored){

        } catch (RemoteException e) {
            e.printStackTrace();
        }

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



    /*
    * Alternar el estado de bandera o escondido, se envia el resultado
    *
    * */
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

        try {
            ClientConnection.sendBox(matchID, isPlayerA, box);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /*
    * este metodo constantemente llama a ClientConnection para preguntar sobre
    * el estado del juego, tablero y la ultima casilla marcada por el rival
    * */

    @Override
    public void run() {
        while (this.gameState == GameState.UNDEFINED){
            if(rival == null){
                try {
                    this.rival = ClientConnection.getRival(matchID, isPlayerA);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            if (rivalBoard == null){
                try {
                    this.rivalBoard = ClientConnection.getRivalBoard(matchID, isPlayerA);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            try {
                Box lastBox = ClientConnection.getRivalBox(matchID, isPlayerA);
                rivalBoard.setVisibleEmptyNeighbours(lastBox.getColumn(), lastBox.getRow());

            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (NullPointerException ignored){

            }

            try {
                this.gameState = ClientConnection.getGameState(matchID);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}

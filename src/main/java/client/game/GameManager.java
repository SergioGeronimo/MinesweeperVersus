package client.game;

import client.connection.ClientConnection;
import client.model.*;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 *Clase exclusiva para administrar las redireccion
 * a la logica del juego o la conexion con los datos
 *
 */
public class GameManager implements Runnable{


    Thread updateInfoThread;
    Player player, rival;
    private boolean isPlayerA;
    private int matchID;
    private Board playerBoard, rivalBoard;
    private int columns, rows, mines;
    private int playerCorrectFlags = 0;
    private MatchState matchState = MatchState.UNDEFINED;
    private MatchDifficulty matchDifficulty;
    private boolean isGameStateFromRival;

    public GameManager() throws RemoteException, NotBoundException, MalformedURLException {
        updateInfoThread = new Thread(this);
        ClientConnection.connectToServer();
    }

    public GameManager(int columns, int rows, int mines) {
        this.columns = columns;
        this.rows = rows;
        this.mines = mines;
        //matchID = -1 indica que no se encuentra un ID legal
        this.matchID = -1;
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

    public MatchState getGameState() {
        return matchState;
    }

    public void setGameState(MatchState matchState) {
        this.matchState = matchState;
    }

    //desconectar del servidor
    public void disconnect() {
        try {
            updateInfoThread.interrupt();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        try {
            ClientConnection.disconnect(player);
        } catch (MalformedURLException | RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }


    public void setMatchReady() {
        try {
            this.matchID = ClientConnection.joinPlayerToMatch(player, matchDifficulty);
        } catch (RemoteException remoteException) {
            remoteException.printStackTrace();
        }


        playerBoard = new Board(columns,rows,mines);
        playerBoard.generateEmptyGrid();
        try {
            updateInfoThread.start();
            isPlayerA = ClientConnection.askIsPlayerA(this.matchID,player.getNickname());
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }
    //interrumpe el hilo para actualizar informacion y crea uno nuevo, es decir "reinicia el hilo"
    public void cancelMatch(){
        updateInfoThread.interrupt();
        updateInfoThread = new Thread(this);
        playerBoard = null;
        try {
            ClientConnection.detachPlayerToMatch(matchID, player);
        } catch (RemoteException remoteException) {
            remoteException.printStackTrace();
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

    //casilla activada, descubrir la casilla y enviarla al servidor
    public void boxActived(int column, int row) {
        try {
            playerBoard.setVisibleEmptyNeighbours(column, row);
            ClientConnection.sendBox(matchID,
                    isPlayerA,
                    playerBoard.getBoxAt(column, row));

        }catch (ArrayIndexOutOfBoundsException ignored){

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        calculateGameState(column, row, false);
        try {
            ClientConnection.sendGameState(this.matchID, this.matchState);
        } catch (RemoteException remoteException) {
            remoteException.printStackTrace();
        }

    }


    /*  calcula el estado del juego de acuerdo a la ultima casilla activada
     *  @param coordenadas de la casilla(column, row) y playerDroppedFlag si en esa casilla se quito una bandera
     */
    private void calculateGameState(int column, int row, boolean playerDroppedFlag) {
        Box box = playerBoard.getBoxAt(column, row);

        //si la casilla activada es una mina, el jugador pierde
        if (box.getValue() == BoxValue.MINE
                    && box.getStatus() == BoxStatus.VISIBLE){
            this.matchState = MatchState.PLAYER_LOST_BY_MINE;

            //si la casilla es una bandera y tiene mina
        }else if (box.getValue() == BoxValue.MINE
                    && box.getStatus() == BoxStatus.FLAGGED){
            //se aumenta el contador de banderas correctas
            playerCorrectFlags++;

            //cuando todas las minas tengan banderas, el jugador gana
            if (playerCorrectFlags == mines){
                this.matchState = MatchState.PLAYER_WON_BY_FLAG;

            }
        //si se quita una bandera correcta, entonces el contador de banderas decrece en uno
        }else if(playerDroppedFlag
                    && box.getValue() == BoxValue.MINE){
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
            ClientConnection.sendGameState(matchID, this.matchState);
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
        MatchState rivalMatchState = MatchState.UNDEFINED;
        while (this.matchState == MatchState.UNDEFINED && rivalMatchState == MatchState.UNDEFINED){
            try {
                if (ClientConnection.askIsMatchReady(matchID)) {
                    if (rival == null) {
                        try {
                            this.rival = ClientConnection.getRival(matchID, isPlayerA);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                    if (rivalBoard == null) {
                        try {
                            this.rivalBoard = ClientConnection.getRivalBoard(matchID, isPlayerA);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        Box lastBox = ClientConnection.getRivalBox(matchID, isPlayerA);
                        if (lastBox != null) {
                            rivalBoard.setVisibleEmptyNeighbours(lastBox.getColumn(), lastBox.getRow());
                        }

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    try {
                        rivalMatchState = ClientConnection.getGameState(matchID);
                        if(rivalMatchState != MatchState.UNDEFINED){
                            isGameStateFromRival = true;
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    Thread.sleep(50);
                }
            } catch (RemoteException remoteException) {
                remoteException.printStackTrace();
            } catch (InterruptedException ignored) {

            }

        }

        if(isGameStateFromRival) {
            switch (rivalMatchState) {
                case PLAYER_LOST_BY_MINE:
                    this.matchState = MatchState.RIVAL_LOST_BY_MINE;
                    break;
                case PLAYER_WON_BY_FLAG:
                    this.matchState = MatchState.RIVAL_WON_BY_FLAG;
                    break;
            }
        }

    }

    public void setDifficulty(MatchDifficulty matchDifficulty) {
        this.matchDifficulty = matchDifficulty;
        switch (matchDifficulty){
            case EASY:
                this.mines = 10;
                this.columns = 10;
                this.rows = 10;
                break;
            case NORMAL:
                this.mines = 40;
                this.columns = 15;
                this.rows = 15;
                break;
            case HARD:
                this.mines = 99;
                this.columns = 30;
                this.rows = 16;
                break;
        }
    }

    public MatchDifficulty getDifficulty(){
        return this.matchDifficulty;
    }

    public boolean isMatchReady() {
        return (rival != null && rivalBoard != null);
    }
}

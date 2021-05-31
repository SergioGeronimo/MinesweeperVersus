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
    private int playerCorrectFlags = 0, playerFlags;
    private MatchState matchState = MatchState.UNDEFINED;
    private MatchDifficulty matchDifficulty;
    private boolean isGameStateFromRival;
    private boolean matchReady, conecctionUnstable;
    

    public GameManager(){
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

    public boolean isConecctionUnstable() {
        return conecctionUnstable;
    }


    public int getMatchID() {
        return matchID;
    }

    public Board getPlayerBoard() {
        return playerBoard;
    }


    public Board getRivalBoard() {
        return rivalBoard;
    }


    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }


    public int getMines() {
        return mines;
    }


    public MatchState getGameState() {
        return matchState;
    }


    public void setServerAddress(String address){
        ClientConnection.setServerAddress(address);
    }

    public boolean connect() throws RemoteException, NotBoundException, MalformedURLException {
        return ClientConnection.connectToServer();
    }

    //desconectar del servidor
    public void disconnect() throws RemoteException, NotBoundException, MalformedURLException {
        try {
            updateInfoThread.interrupt();
        } catch (Exception exception) {
            restartInfoThread();
        }
        ClientConnection.disconnect(player);

    }

    private void restartInfoThread() {
        updateInfoThread = new Thread(this);
    }


    public void setMatchReady() throws RemoteException {
        this.matchID = ClientConnection.joinPlayerToMatch(player, matchDifficulty);


        playerBoard = new Board(columns, rows, mines);
        playerBoard.generateEmptyGrid();

        updateInfoThread.start();
        isPlayerA = ClientConnection.askIsPlayerA(this.matchID, player.getNickname());


    }
    //interrumpe el hilo para actualizar informacion y crea uno nuevo, es decir "reinicia el hilo"
    public void cancelMatch() throws RemoteException {
        updateInfoThread.interrupt();
        updateInfoThread = new Thread(this);
        playerBoard = null;
        if (this.matchState == MatchState.UNDEFINED){
            this.matchState = MatchState.PLAYER_SURRENDER;
            ClientConnection.sendGameState(this.matchID, this.matchState);
        }

        ClientConnection.detachPlayerToMatch(matchID, player);

    }


    //genera minas y los indicadores
    public void startMatchAt(int column, int row) throws RemoteException {
        playerBoard.generateMines(column, row);
        playerBoard.fillNearIndicators();

            ClientConnection.sendBoard(matchID, isPlayerA, playerBoard) ;
    }

    //casilla activada, descubrir la casilla y enviarla al servidor
    public void boxActived(int column, int row) throws RemoteException {
            playerBoard.setVisibleEmptyNeighbours(column, row);
            ClientConnection.sendBox(matchID,
                    isPlayerA,
                    playerBoard.getBoxAt(column, row));

        calculateGameState(column, row, false);
            ClientConnection.sendGameState(this.matchID, this.matchState);

    }


    /*  calcula el estado del juego de acuerdo a la ultima casilla activada
     *  @param coordenadas de la casilla(column, row) y flagAdded si en esa casilla se añadio una bandera
     */
    private void calculateGameState(int column, int row, boolean flagAdded) {
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
            System.out.println("correctfalgs = " +playerCorrectFlags);
            System.out.println("playerFlags = " + playerFlags);
            //cuando todas las minas tengan banderas, el jugador gana
            if (playerCorrectFlags == mines && playerFlags == mines){
                this.matchState = MatchState.PLAYER_WON_BY_FLAG;

            }
        //si se quita una bandera correcta, entonces el contador de banderas decrece en uno
        }else if(!flagAdded
                    && box.getValue() == BoxValue.MINE){
            playerCorrectFlags--;
        }
    }



    /*
    * Alternar el estado de bandera o escondido, se envia el resultado
    * @returns verdadero si se añade una bandera, falso si se quita una
    *
    * */
    public boolean toggleFlagStatus(int column, int row) throws RemoteException {
        Box box = playerBoard.getBoxAt(column, row);
        switch (box.getStatus()){
            case HIDDEN:
                box.setStatus(BoxStatus.FLAGGED);
                playerFlags++;
                break;
            case FLAGGED:
                box.setStatus(BoxStatus.HIDDEN);
                playerFlags--;
                break;
        }
        boolean flagAdded = box.getStatus() == BoxStatus.FLAGGED;
        calculateGameState(column, row, flagAdded);
        ClientConnection.sendBox(matchID, isPlayerA, box);
        ClientConnection.sendGameState(matchID, this.matchState);

        return flagAdded;
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
                    matchReady = true;
                    if (rival == null) {
                        try {
                            this.rival = ClientConnection.getRival(matchID, isPlayerA);
                        } catch (RemoteException e) {
                            this.conecctionUnstable = true;
                        }
                    }

                    if (rivalBoard == null) {
                        try {
                            this.rivalBoard = ClientConnection.getRivalBoard(matchID, isPlayerA);
                        } catch (RemoteException e) {
                            this.conecctionUnstable = true;
                        }
                    }

                    try {
                        Box lastBox = ClientConnection.getRivalBox(matchID, isPlayerA);
                        if (lastBox != null) {
                            if(lastBox.getStatus() == BoxStatus.FLAGGED){
                                rivalBoard.setBoxStatus(lastBox.getColumn(), lastBox.getRow(), BoxStatus.FLAGGED);
                            }else {
                                rivalBoard.setVisibleEmptyNeighbours(lastBox.getColumn(), lastBox.getRow());
                            }
                        }

                    } catch (RemoteException e) {
                        this.conecctionUnstable = true;;
                    }

                    try {
                        rivalMatchState = ClientConnection.getGameState(matchID);
                        if(rivalMatchState != MatchState.UNDEFINED){
                            isGameStateFromRival = true;
                        }
                    } catch (RemoteException e) {
                        this.conecctionUnstable = true;;
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
                case PLAYER_SURRENDER:
                    this.matchState = MatchState.RIVAL_SURRENDER;
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
                this.columns = 20;
                this.rows = 20;
                break;
        }
    }

    public MatchDifficulty getDifficulty(){
        return this.matchDifficulty;
    }

    public boolean isMatchReady() {
        return matchReady;
    }
}

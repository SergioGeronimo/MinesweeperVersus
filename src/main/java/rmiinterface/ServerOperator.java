package rmiinterface;

import client.game.GameState;
import client.model.Board;
import client.model.Box;
import client.model.Match;
import client.model.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerOperator extends Remote {

    public boolean addClient(Player player) throws RemoteException;

    public int joinPlayerToMatch(Player player) throws RemoteException;

    public void detachPlayerToMatch(int matchId, String nickname) throws RemoteException;

    public Board getRivalBoard(int matchID, boolean isPlayerA) throws RemoteException;

    public void sendBoard(int matchId, boolean isPLayerA, Board board) throws RemoteException;

    public Box getRivalBox(int matchID, boolean isPLayerA) throws RemoteException;

    public void sendBox(int matchId, boolean isPlayerA, Box box) throws RemoteException;

    public GameState getGameState(int matchID) throws RemoteException;

    public boolean isMatchReady(int matchID) throws RemoteException;

    public boolean askIsPlayerA(int matchID, String nickname) throws RemoteException;

    Player getRival(int matchID, boolean isPlayerA) throws RemoteException;

    void detachPlayer(Player player) throws RemoteException;

    void setGameState(int matchID, GameState gameState) throws RemoteException;
}

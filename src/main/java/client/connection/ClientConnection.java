package client.connection;

import client.game.MatchState;
import client.model.Board;
import client.model.Box;
import client.model.MatchDifficulty;
import client.model.Player;
import rmiinterface.ServerOperator;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ClientConnection {
    private static ServerOperator serverOperator;
    private static final String SERVER_NAME = "//192.168.56.1:4444/MinesweeperServer";

    public static boolean connectToServer() throws
            MalformedURLException, RemoteException, NotBoundException {
        serverOperator = (ServerOperator) Naming.lookup(SERVER_NAME);
        return serverOperator != null;
    }

    public static void disconnect(Player player) throws MalformedURLException, RemoteException, NotBoundException{
        serverOperator.detachPlayer(player);
        //Naming.unbind(SERVER_NAME);
    }

    public static void detachPlayerToMatch(int matchID, Player player) throws RemoteException{
        serverOperator.detachPlayerToMatch(matchID, player.getNickname());
    }

    public static boolean addClient(Player player) {
        boolean successAdd = false;
        try {
            successAdd = serverOperator.addClient(player);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return successAdd;
    }

    public static int joinPlayerToMatch(Player player, MatchDifficulty matchDifficulty) throws RemoteException {
        return serverOperator.joinPlayerToMatch(player, matchDifficulty);
    }

    public static boolean askIsPlayerA(int matchID, String nickname) throws RemoteException {
        return serverOperator.askIsPlayerA(matchID, nickname);
    }

    public static void sendBoard(int matchID, boolean isPlayerA, Board playerBoard) throws RemoteException {
        serverOperator.sendBoard(matchID, isPlayerA, playerBoard);
    }

    public static void sendBox(int matchID, boolean isPlayerA, Box box) throws RemoteException {
        serverOperator.sendBox(matchID, isPlayerA, box);
    }

    public static Board getRivalBoard(int matchID, boolean isPlayerA) throws RemoteException{
        return serverOperator.getRivalBoard(matchID, isPlayerA);
    }

    public static Player getRival(int matchID, boolean isPlayerA) throws RemoteException{
        return serverOperator.getRival(matchID, isPlayerA);
    }

    public static Box getRivalBox(int matchID, boolean isPlayerA) throws RemoteException{
        return serverOperator.getRivalBox(matchID, isPlayerA);
    }

    public static MatchState getGameState(int matchID) throws RemoteException{
        return  serverOperator.getGameState(matchID);
    }

    public static boolean askIsMatchReady(int matchId) throws RemoteException{
        return serverOperator.isMatchReady(matchId);
    }

    public static void sendGameState(int matchID, MatchState matchState) throws RemoteException {
        serverOperator.setGameState(matchID, matchState);
    }
}

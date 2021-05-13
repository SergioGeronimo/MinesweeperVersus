package client.connection;

import client.game.GameState;
import client.model.Board;
import client.model.Box;
import client.model.Player;
import rmiinterface.ServerRemote;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ClientConnection {
    private static ServerRemote LOOK_UP;
    private static final String SERVER_NAME = "//192.168.0.4:4444/MinesweeperServer";

    public static boolean connectToServer() throws
            MalformedURLException, RemoteException, NotBoundException {
        LOOK_UP = (ServerRemote) Naming.lookup(SERVER_NAME);
        return LOOK_UP != null;


    }

    public static void disconnect() throws MalformedURLException, RemoteException, NotBoundException{
        Naming.unbind(SERVER_NAME);
    }

    public static boolean addClient(Player player) {
        boolean successAdd = false;
        try {
            successAdd = LOOK_UP.addClient(player);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return successAdd;
    }

    public static int joinPlayerToMatch(Player player) throws RemoteException {
        return LOOK_UP.joinPlayerToMatch(player);
    }

    public static boolean askIsPlayerA(int matchID, String nickname) throws RemoteException {
        return LOOK_UP.askIsPlayerA(matchID, nickname);
    }

    public static void sendBoard(int matchID, boolean isPlayerA, Board playerBoard) throws RemoteException {
        LOOK_UP.sendBoard(matchID, isPlayerA, playerBoard);
    }

    public static void sendBox(int matchID, boolean isPlayerA, Box box) throws RemoteException {
        System.err.println(box.getColumn() +", " + box.getRow() +", " + box.getValue().toString() +", "+box.getStatus().toString());
        LOOK_UP.sendBox(matchID, isPlayerA, box);
    }

    public static Board getRivalBoard(int matchID, boolean isPlayerA) throws RemoteException{
        return LOOK_UP.getRivalBoard(matchID, isPlayerA);
    }

    public static Player getRival(int matchID, boolean isPlayerA) throws RemoteException{
        return LOOK_UP.getRival(matchID, isPlayerA);
    }

    public static Box getRivalBox(int matchID, boolean isPlayerA) throws RemoteException{
        return LOOK_UP.getRivalBox(matchID, isPlayerA);
    }

    public static GameState getGameState(int matchID) throws RemoteException{
        return  LOOK_UP.getGameState(matchID);
    }
}

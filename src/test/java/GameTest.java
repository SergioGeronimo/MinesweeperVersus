import client.connection.ClientConnection;
import client.game.GameManager;
import client.model.MatchDifficulty;
import client.model.Player;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class GameTest {

    private static Player player;
    private static GameManager gameManager;
    private static boolean isPlayerA;

    public static void main(String[] args) {
        player = new Player("test player");
        gameManager = new GameManager();

        gameManager.setPlayer(player);
        gameManager.setDifficulty(MatchDifficulty.EASY);

        try {
            ClientConnection.connectToServer();
            ClientConnection.addClient(player);
            gameManager.setMatchID(ClientConnection.joinPlayerToMatch(player));
            isPlayerA = ClientConnection.askIsPlayerA(gameManager.getMatchID(), player.getNickname());

            gameManager.setMatchReady();
            gameManager.startMatchAt(4,4);
            gameManager.revealPlayerBoxValue(4,4);

            ClientConnection.sendBoard(gameManager.getMatchID(), isPlayerA, gameManager.getPlayerBoard());



        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

}

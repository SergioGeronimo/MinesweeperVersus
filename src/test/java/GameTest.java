import client.connection.ClientConnection;
import client.game.GameManager;
import client.model.MatchDifficulty;
import client.model.Player;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Random;

public class GameTest {

    private static Player player;
    private static GameManager gameManager;
    private static boolean isPlayerA;

    public static void randomPlay() throws RemoteException {
        Random random = new Random();
        int x, y;
        for (int i =0; i < 100; i++){
            x = random.nextInt(10);
            y = random.nextInt(10);

            if (random.nextInt(10) == 4){
                gameManager.toggleFlagStatus(x, y);
            }else {
                gameManager.boxActived(x, y);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        player = new Player("test bot");
        gameManager = new GameManager();

        gameManager.setPlayer(player);
        gameManager.setDifficulty(MatchDifficulty.EASY);

        try {
            gameManager.setServerAddress("192.168.56.1");
            ClientConnection.connectToServer();
            ClientConnection.addClient(player);


            gameManager.setMatchReady();
            isPlayerA = ClientConnection.askIsPlayerA(gameManager.getMatchID(), player.getNickname());
            gameManager.startMatchAt(4,4);
            gameManager.boxActived(4,4);
            gameManager.toggleFlagStatus(5,5);

            ClientConnection.sendBoard(gameManager.getMatchID(), isPlayerA, gameManager.getPlayerBoard());
            //randomPlay();
            //gameManager.disconnect();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

}

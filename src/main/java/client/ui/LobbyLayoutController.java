package client.ui;

import client.connection.ClientConnection;
import client.game.GameManager;
import client.model.Player;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class LobbyLayoutController {
    @FXML
    private TextField nickname;
    Player player;
    GameManager gameManager;


    public void connect(MouseEvent mouseEvent) {
        Scene scene = ((Node) mouseEvent.getSource()).getScene();
        boolean connectionSuccess = false;

        player = new Player(nickname.getText());
        try {
            connectionSuccess = ClientConnection.connectToServer();
            int match = ClientConnection.joinPlayerToMatch(player);

        } catch (MalformedURLException | RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        if(connectionSuccess){
            changeSceneToGame(scene);
        }

    }

    //Cambia escena del lobby al juego, pasa toda
    //la infromacion necesario al siguiente controlador
    public void changeSceneToGame(Scene scene){
        Parent root = null;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layout/gameLayout.fxml"));

        try {
            root = fxmlLoader.load();
            scene.setRoot(root);
            GameLayoutController controller = (GameLayoutController) fxmlLoader.getController();
            controller.getGameManager().setMatchID(this.gameManager.getMatchID());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }

}

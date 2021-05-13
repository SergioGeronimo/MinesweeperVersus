package client.ui.controller;

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

public class LobbyController {
    @FXML
    private TextField nickname;
    Player player;
    GameManager gameManager;

    @FXML
    public void initialize(){
        gameManager = new GameManager(10,10,10);
    }

    /*
    * pide la conexion con el servidor, le añade al game manager la informacion del jugador y el id de la partida
    * cambiar para que el game manager se encarge de esto
    *
    * */
    public void connect(MouseEvent mouseEvent) {
        Scene scene = ((Node) mouseEvent.getSource()).getScene();
        boolean connectionSuccess = false;

        player = new Player(nickname.getText());
        try {
            connectionSuccess = ClientConnection.connectToServer();
            int matchID = ClientConnection.joinPlayerToMatch(player);
            gameManager.setMatchID(matchID);
            gameManager.setPlayer(player);


        } catch (MalformedURLException | RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        if(connectionSuccess){
            changeSceneToSelect(scene);
        }

    }

    //Cambia escena del lobby a la seleccion de juego, pasa toda
    //la infromacion necesario al siguiente controlador
    public void changeSceneToSelect(Scene scene){
        Parent root = null;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layout/gameSelect.fxml"));

        try {
            root = fxmlLoader.load();

            GameSelectController controller = (GameSelectController) fxmlLoader.getController();
            controller.setGameManager(gameManager);
            controller.setScene(scene);
            scene.setRoot(root);
            controller.updateLabels();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }

}

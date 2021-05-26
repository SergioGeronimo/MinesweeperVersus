package client.ui.controller;

import client.model.Player;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.io.IOException;

/*
* Controlador de la pantalla de inicio
*
* */

public class StartMenuController extends Controller{


    @FXML
    private GridPane mainContainer;
    Player player;

    //Cambia escena del inicio a la seleccion de juego, pasa toda
    //la infromacion necesario al siguiente controlador
    public void changeSceneToSelect(MouseEvent mouseEvent){

        Parent root;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layout/gameSelect.fxml"));

        try {
            root = fxmlLoader.load();

            GameSelectController controller = fxmlLoader.getController();
            controller.setGameManager(getGameManager());

            controller.setScene(getScene());
            getScene().setRoot(root);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }

}

package client.ui.controller;

import client.model.Player;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

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
        setNextScenePath("/layout/client/gameSelect.fxml");
        toNextScene();
    }
}

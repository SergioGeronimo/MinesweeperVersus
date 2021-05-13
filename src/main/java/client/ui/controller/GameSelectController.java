package client.ui.controller;

import client.game.GameManager;
import client.model.MatchDifficulty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class GameSelectController {

    Scene scene;
    @FXML
    private Label nickname;
    private GameManager gameManager;

    public GameManager getGameManager() {
        return gameManager;
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;

    }

    public void setEasyGame(MouseEvent mouseEvent) {
        gameManager.setDifficulty(MatchDifficulty.EASY);
        if(gameManager.isMatchReady()) {
            toGameScene();
        }
    }

    public void setNormalGame(MouseEvent mouseEvent) {
        gameManager.setDifficulty(MatchDifficulty.NORMAL);
        toGameScene();
    }

    public void setHardGame(MouseEvent mouseEvent) {
        gameManager.setDifficulty(MatchDifficulty.HARD);
        if(gameManager.isMatchReady()) {
            toGameScene();
        }
    }


    //Cambia escena del lobby al juego, pasa toda
    //la infromacion necesario al siguiente controlador
    public void toGameScene(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layout/game.fxml"));
        gameManager.setMatchReady();

        try {
            Parent root = (Parent) fxmlLoader.load();
            GameController gameController = fxmlLoader.getController();
            gameController.setGameManager(this.gameManager);
            gameController.setMatchReady();
            gameController.updateLabels();
            scene.setRoot(root);

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void updateLabels() {
        nickname.setText(gameManager.getPlayer().getNickname());
    }


    public void setScene(Scene scene) {
        this.scene = scene;
    }
}

package client.ui.controller;

import client.game.GameManager;
import javafx.scene.Scene;

public class Controller {
    private Scene scene;
    private GameManager gameManager;

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }
}

package client.ui.controller;

import client.game.GameManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Window;

import java.io.IOException;

public class Controller {
    private Scene scene;
    private GameManager gameManager;
    private String nextScenePath;

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

    public String getNextScenePath() {
        return nextScenePath;
    }

    public void setNextScenePath(String nextScenePath) {
        this.nextScenePath = nextScenePath;
    }

    public void resizeWindow(int width, int height){
        Window sceneWindow = scene.getWindow();
        sceneWindow.setWidth(width);
        sceneWindow.setHeight(height);
        sceneWindow.centerOnScreen();
    }

    public Controller toNextScene(){
        Parent root;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(nextScenePath));
        Controller controller = null;
        try {
            root = fxmlLoader.load();

            controller = fxmlLoader.getController();
            controller.setGameManager(getGameManager());

            controller.setScene(getScene());
            getScene().setRoot(root);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        return controller;
    }
}

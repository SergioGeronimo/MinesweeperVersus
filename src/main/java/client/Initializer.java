package client;

import client.game.GameManager;
import client.ui.controller.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Initializer extends Application {

    private static String fxmlPath = "/layout/startMenu.fxml";

    private static GameManager gameManager;

    @Override
    public void start(Stage stage) throws Exception {
        //leer el layout de inicio
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = fxmlLoader.load();

        //obtener el controlador
        Controller controller = fxmlLoader.getController();
        //añadir el gamemanager al controlador
        controller.setGameManager(gameManager);


        //ajustes de ventana
        stage.setTitle("Minesweeper versus");
        stage.setScene(new Scene(root, 480, 852));
        controller.setScene(stage.getScene());
        stage.centerOnScreen();
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        gameManager.disconnect();
        System.exit(0);
    }

    public static void main(String[] args) {
        try {
            gameManager = new GameManager();
        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            e.printStackTrace();
        }
        launch(args);
    }
}

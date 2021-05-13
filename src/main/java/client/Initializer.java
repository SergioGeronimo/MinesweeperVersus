package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Initializer extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/layout/lobby.fxml"));
        stage.setTitle("Minesweeper versus");
        stage.setScene(new Scene(root, 1280, 720));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

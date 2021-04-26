package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Initializer extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/gameLayout.fxml"));
        stage.setTitle("Minesweeper versus");
        stage.setScene(new Scene(root, 1024, 576));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

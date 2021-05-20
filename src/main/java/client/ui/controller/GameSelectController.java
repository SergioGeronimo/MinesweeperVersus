package client.ui.controller;

import client.model.MatchDifficulty;
import client.model.Player;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.FileInputStream;
import java.io.IOException;

public class GameSelectController extends Controller{
    @FXML
    private Button startButton, easyButton, normalButton, hardButton;
    @FXML
    private VBox infoPane;
    @FXML
    private GridPane mainContainer;
    @FXML
    private TextField nickname;

    private Label infoLabel;
    private ImageView waitingImage;

    private Task<Void> uiTask;
    private Thread uiThread;


    @FXML
    public void initialize(){
        infoLabel = new Label();
        waitingImage = new ImageView("/icons/mine.png");

        uiThread = new Thread(()->{
            Platform.runLater( () ->{

                infoPane.getChildren().addAll(waitingImage, infoLabel);

                infoLabel.setText("Buscando partida");
                infoPane.getStyleClass().add("waiting");
                nickname.setEditable(false);
                easyButton.setDisable(true);
                normalButton.setDisable(true);
                hardButton.setDisable(true);
            } );

            while (!getGameManager().isMatchReady()){
                waitingImage.setRotate(waitingImage.getRotate() + 15);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("match found");
            Platform.runLater( () -> {
                infoPane.getStyleClass().remove("waiting");
                infoPane.getStyleClass().add("found");
                infoLabel.setText("Partida encontrada");
            });

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(this::toGameScene);

        });
    }


    public void setEasyGame(MouseEvent mouseEvent) {

        easyButton.getStyleClass().add("easy-selected");
        normalButton.getStyleClass().remove("normal-selected");
        hardButton.getStyleClass().remove("hard-selected");

        getGameManager().setDifficulty(MatchDifficulty.EASY);
        startButton.setDisable(false);
    }

    public void setNormalGame(MouseEvent mouseEvent) {

        easyButton.getStyleClass().remove("easy-selected");
        normalButton.getStyleClass().add("normal-selected");
        hardButton.getStyleClass().remove("hard-selected");

        getGameManager().setDifficulty(MatchDifficulty.NORMAL);
        startButton.setDisable(false);
    }

    public void setHardGame(MouseEvent mouseEvent) {

        easyButton.getStyleClass().remove("easy-selected");
        normalButton.getStyleClass().remove("normal-selected");
        hardButton.getStyleClass().add("hard-selected");

        getGameManager().setDifficulty(MatchDifficulty.HARD);
        startButton.setDisable(false);
    }


    //Cambia escena del lobby al juego, pasa toda
    //la infromacion necesario al siguiente controlador
    public void toGameScene(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layout/game.fxml"));

        try {
            Parent root = fxmlLoader.load();
            GameController gameController = fxmlLoader.getController();
            gameController.setGameManager( getGameManager());
            gameController.setMatchReady();
            gameController.updateLabels();
             getScene().setRoot(root);
             getScene()
                     .getWindow()
                     .setWidth(1280);
             getScene()
                     .getWindow()
                     .setHeight(720);
             getScene()
                     .getWindow()
                     .centerOnScreen();

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }


    public void lookForMatch(MouseEvent actionEvent) {
        if (validateStartCondition()){
            getGameManager()
                    .setPlayer(
                    new Player(nickname.getText())
                    );

            nickname
                    .getStyleClass()
                    .remove("required");

            startButton.setText("Cancel");
            startButton.setOnMouseClicked(this::cancelMatch);
            getGameManager().setMatchReady();

            uiThread.start();

        }else {
            nickname.getStyleClass()
                    .add("required");
        }
    }

    public void cancelMatch(MouseEvent mouseEvent){
        startButton.setText("Buscar partida");
        startButton.setOnMouseClicked(this::lookForMatch);
        uiThread.interrupt();
        initialize();
        nickname.setEditable(true);
        easyButton.setDisable(false);
        normalButton.setDisable(false);
        hardButton.setDisable(false);
    }


    public boolean validateStartCondition(){
        return !nickname.getText().equals("") && getGameManager().getDifficulty() != null;
    }
}

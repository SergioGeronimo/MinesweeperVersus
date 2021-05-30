package client.ui.controller;

import client.model.MatchDifficulty;
import client.model.Player;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

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

    /*
    * Se llama al leer el archivo FXML, lee los iconos necesario para la animacion de carga
    * crea el hilo especial para actualizar la interfaz, pero no lo inicia
    * */
    @FXML
    public void initialize(){
        infoLabel = new Label();
        waitingImage = new ImageView("/icons/mine.png");

        infoPane.getChildren().add(infoLabel);

        uiThread = new Thread(()->{
            Platform.runLater( () ->{

                infoPane.getChildren().add(0, waitingImage);

                infoLabel.setText("Buscando partida");
                infoPane.getStyleClass().add("waiting");
                nickname.setEditable(false);
                easyButton.setDisable(true);
                normalButton.setDisable(true);
                hardButton.setDisable(true);
            } );

            while (!getGameManager().isMatchReady()){
                waitingImage.setRotate(waitingImage.getRotate() + 5);

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.err.println("ui thread failed: " + e.getMessage());
                }
            }

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

    /*
    * Pone la dificultad a facil
    * */
    public void setEasyGame(MouseEvent mouseEvent) {

        easyButton.getStyleClass().add("easy-selected");
        normalButton.getStyleClass().remove("normal-selected");
        hardButton.getStyleClass().remove("hard-selected");

        try {
            getGameManager().setDifficulty(MatchDifficulty.EASY);
            startButton.setDisable(false);
        }catch (NullPointerException exception){
            infoLabel.setText("Sin conexion al servidor");
        }
    }
    /*
    * Pone la dificultad a normal
    * */
    public void setNormalGame(MouseEvent mouseEvent) {

        easyButton.getStyleClass().remove("easy-selected");
        normalButton.getStyleClass().add("normal-selected");
        hardButton.getStyleClass().remove("hard-selected");

        try {
            getGameManager().setDifficulty(MatchDifficulty.NORMAL);
            startButton.setDisable(false);
        }catch (NullPointerException exception){
            infoLabel.setText("Sin conexion al servidor");
        }
    }
    /*
    * Pone la dificultad a dificil
    * */
    public void setHardGame(MouseEvent mouseEvent) {

        easyButton.getStyleClass().remove("easy-selected");
        normalButton.getStyleClass().remove("normal-selected");
        hardButton.getStyleClass().add("hard-selected");

        getGameManager().setDifficulty(MatchDifficulty.HARD);
        startButton.setDisable(false);
    }


    //Cambia escena de la seleccion al juego, pasa toda
    //la informacion necesario al siguiente controlador
    public void toGameScene(){
        setNextScenePath("/layout/client/game.fxml");
        GameController controller = (GameController) toNextScene();
        controller.setMatchReady();
    }

    /*
    * Pone en espera para iniciar el juego, preguntando a GameManager si esta listo
    * Inicia el hilo para la animacion de espera
    * */
    public void lookForMatch(MouseEvent actionEvent) {
        if (validateStartCondition()){
            getGameManager()
                    .setPlayer(
                    new Player(nickname.getText())
                    );

            nickname
                    .getStyleClass()
                    .remove("required");

            startButton.setText("Cancelar");
            startButton.setOnMouseClicked(this::cancelMatch);
            getGameManager().setMatchReady();

            uiThread.start();

        }else {
            nickname.getStyleClass()
                    .add("required");
        }
    }


    /*
    * cancela la busqueda de partida
    * */
    public void cancelMatch(MouseEvent mouseEvent){
        startButton.setText("Buscar partida");
        startButton.setOnMouseClicked(this::lookForMatch);
        uiThread.interrupt();
        initialize();
        getGameManager().cancelMatch();
        nickname.setEditable(true);
        easyButton.setDisable(false);
        normalButton.setDisable(false);
        hardButton.setDisable(false);
    }

    /*
    * Validacion del nombre de usuario y dificultad
    * */
    public boolean validateStartCondition(){
        return !nickname.getText().equals("") && getGameManager().getDifficulty() != null;
    }
}

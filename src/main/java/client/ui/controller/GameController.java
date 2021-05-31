package client.ui.controller;

import client.game.MatchState;
import client.ui.customcontrol.GameBoxButton;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import client.model.Board;
import client.model.Box;
import client.model.BoxValue;
import javafx.stage.Stage;

import java.rmi.RemoteException;

public class GameController extends Controller{
    @FXML
    private Button screenButton;
    @FXML
    private Pane resultBanner;
    @FXML
    private Label connectionInfo, resultLabel, resultInfo, playerLabel, playerFlagsLabel, rivalLabel;
    @FXML
    private GridPane resultContainer, mainContainer, rivalBoardContainer, playerBoardContainer;
    @FXML
    private VBox gameMenu;

    private int playerFlagsLeftCount, rivalFlagsCount;

    private GameBoxButton[][] playerBoxButtons, rivalBoxButtons;

    /*
    * Inicia el hilo para actualizar la interfaz grafica
    *
    * */
    public void startUIUpdateThread(){
        Task<Void> voidTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                updateUI();
                return null;
            }
        };

        new Thread(voidTask).start();
    }

    /*
    * llama al GameManager para iniciar partida,
    * prepara el gridpane llenando cada casilla con botones
    * si el gridpane es del rival, se desabilita.
    *
    * */
    public void setMatchReady() {

        playerBoxButtons = new GameBoxButton[getGameManager().getRows()][getGameManager().getColumns()];
        rivalBoxButtons = new GameBoxButton[getGameManager().getRows()][getGameManager().getColumns()];

        fillGridPaneWithButtons(
                playerBoardContainer,
                playerBoxButtons,
                false);

        fillGridPaneWithButtons(
                rivalBoardContainer,
                rivalBoxButtons,
                true);

        playerFlagsLeftCount = getGameManager().getMines();
    }

    /*
    * Se notifica al GameManager la casilla a empezar partida
    * todos los bottones cambian de listener para empezar partida
    * a listener para descubrir/abanderar casillas
    */
    public void startMatch(MouseEvent mouseEvent){
        GameBoxButton source = (GameBoxButton) mouseEvent.getSource();

        try {
            getGameManager().startMatchAt(source.getColumn(), source.getRow());
        } catch (RemoteException remoteException) {
            connectionInfo.setVisible(true);
        }


        for (int rowIndex = 0; rowIndex < playerBoxButtons.length; rowIndex++) {
            for (int colIndex = 0; colIndex < playerBoxButtons[0].length; colIndex++) {

                playerBoxButtons[rowIndex][colIndex]
                        .setBox(getGameManager().getPlayerBoard().getBoxAt(colIndex, rowIndex));
                playerBoxButtons[rowIndex][colIndex]
                        .setOnMouseClicked(this::handleBoxClicked);


            }
        }

        startUIUpdateThread();
        handleBoxClicked(mouseEvent);

    }

    /*
    * Actualiza la clase CSS de cada boton y su texto segun el valor,
    * este metodo se debe correr en hilo
    *
    * si el boton es una bandera se marca como flagged
    * si tiene un valor 0 a 8 se le pone como clase value-[valor]
    * ej: valor 4, clase CSS = value-4
    * si es mina no se le pone el texto al boton aun tiene clase value-x
    * ej valor x (es decir es mina), clase CSS = value-x
    *
    * las casillas escondidas no son actualizadas
    * */
    public void updateUI() throws InterruptedException {
        boolean keepRunning = true;

        Board playerBoard = getGameManager().getPlayerBoard();
        Board[] allBoards = {getGameManager().getPlayerBoard(), getGameManager().getRivalBoard()};
        GameBoxButton[][][] allButtons = {playerBoxButtons, rivalBoxButtons};

        do{
                Platform.runLater(() -> {
                    playerLabel.setText(getGameManager().getPlayer().getNickname());
                    rivalLabel.setText(getGameManager().getRival().getNickname());
                    playerFlagsLabel.setText("" + playerFlagsLeftCount);

                });

            for (int boardIndex = 0; boardIndex < allBoards.length; boardIndex++) {
                for (int rowIndex = 0; rowIndex < getGameManager().getRows(); rowIndex++) {
                    for (int colIndex = 0; colIndex < getGameManager().getColumns(); colIndex++) {


                        Box box = allBoards[boardIndex].getBoxAt(colIndex, rowIndex);
                        GameBoxButton gameBoxButton = allButtons[boardIndex][rowIndex][colIndex];


                        switch (box.getStatus()) {
                            case FLAGGED:
                                //bugfix ui_1: busca si la casilla ya tenia la clase asi no tiene la clase dos veces
                                if (!gameBoxButton.getStyleClass().contains("flagged"))
                                    Platform.runLater(()->{
                                        gameBoxButton.getStyleClass().add("flagged");
                                    });
                                break;
                            case VISIBLE:
                                String value = box.getValue().getValue();

                                Platform.runLater(()->{


                                gameBoxButton.getStyleClass().remove("flagged");
                                gameBoxButton.getStyleClass().add("value-" + value);
                                gameBoxButton.setDisable(true);
                                });

                                if (!value.equals(BoxValue.MINE.getValue())) {
                                    //cambios directos en ui dentro de un hilo
                                    // se deben ejecutar con Platform.runLater()
                                    Platform.runLater(() -> {
                                        gameBoxButton.setText(value);
                                    });

                                }
                                break;
                            case HIDDEN:
                                //bugfix ui_1: elimina las banderas
                                Platform.runLater(() -> {
                                    gameBoxButton.getStyleClass().remove("flagged");
                                    gameBoxButton.setText("");
                                });

                                break;

                        }

                        if(getGameManager().getGameState() != MatchState.UNDEFINED){
                            keepRunning = false;
                        }
                    }
                }
            }
            connectionInfo.setVisible( getGameManager().isConecctionUnstable() );

            Thread.sleep(100);
        }while (keepRunning);
        showResultsBanner();

    }

    private void showResultsBanner() {
        resultContainer.setVisible(true);
        MatchState matchState = getGameManager().getGameState();

        if (    matchState == MatchState.RIVAL_WON_BY_FLAG ||
                matchState == MatchState.PLAYER_SURRENDER ||
                matchState == MatchState.PLAYER_LOST_BY_MINE){
            Platform.runLater(()->{
                resultBanner.getStyleClass().add("defeat");
                resultLabel.setText("Derrota");
            });


        }else {
            Platform.runLater(() ->{
                resultBanner.getStyleClass().add("victory");
                resultLabel.setText("Victoria");
            });

        }
        Platform.runLater(()->{
            resultInfo.setText(matchState.toString());
        });
    }



    /*
    * Decide que hacer con clic primario o secundario,
    * si es primario se revela el valor
    * si es secundario se alterna su estado de bandera
    * */

    public void handleBoxClicked(MouseEvent mouseEvent){
        GameBoxButton boxButtonSource = (GameBoxButton) mouseEvent.getSource();
        int boxColumn = boxButtonSource.getColumn();
        int boxRow = boxButtonSource.getRow();

        try {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                getGameManager().boxActived(boxColumn, boxRow);

            } else {
                boolean flagAdded = getGameManager().toggleFlagStatus(boxColumn, boxRow);
                if (flagAdded){
                    playerFlagsLeftCount--;
                }else {
                    playerFlagsLeftCount++;
                }

            }
        }catch (RemoteException e){
            connectionInfo.setVisible(true);
        }


    }

    /*
    * Del gridpane seleccionado crea las columnas y renglones indicados
    * de forma que son uniformes al tamaño dsiponible y responsivos
    * los llena de botones con informacion de su ubicacion (columna, renglon)
    *
    * los botones son hijos de la clase javafx.controls.Button
    * */
    public void fillGridPaneWithButtons(GridPane boardContainer, GameBoxButton[][] boxButtonsArray, boolean isDisabled){

        boardContainer.getChildren().clear();
        int columns = getGameManager().getColumns();
        int rows = getGameManager().getRows();


        ObservableList<ColumnConstraints> containerColumnConstraints = boardContainer.getColumnConstraints();

        containerColumnConstraints.clear();
        for (int colCount = 0; colCount < columns; colCount++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setHgrow(Priority.ALWAYS);


            if(boardContainer.getWidth() > boardContainer.getHeight())
                columnConstraints.setPercentWidth(1.0/rows * 100);
            else
                columnConstraints.setPercentWidth( 1.0/columns * 100);


            containerColumnConstraints.add(columnConstraints);

        }


        ObservableList<RowConstraints> containerRowConstraints = boardContainer.getRowConstraints();
        containerRowConstraints.clear();
        for (int rowCount = 0; rowCount < rows; rowCount++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setVgrow(Priority.ALWAYS);
            rowConstraints.setPercentHeight(1.0/rows * 100);
            containerRowConstraints.add(rowConstraints);

        }


        for (int rowCount = 0; rowCount < rows; rowCount++) {
            for (int colCount = 0; colCount < columns; colCount++) {

                GameBoxButton boxButton = new GameBoxButton(colCount, rowCount);
                boxButton
                        .getStyleClass()
                        .add("game-box");

                boxButtonsArray[rowCount][colCount] = boxButton;

                if (isDisabled){
                    boxButton.setDisable(true);

                }else {
                    boxButton.setOnMouseClicked(this::startMatch);

                }

                boardContainer.add(boxButton, colCount, rowCount);
            }
        }

    }

    public void endMatch(MouseEvent mouseEvent) {
        try {
            getGameManager().cancelMatch();
        } catch (RemoteException remoteException) {
            connectionInfo.setVisible(true);
        }
        setNextScenePath("/layout/client/gameSelect.fxml");
    }

    public void enterFullscreen(MouseEvent mouseEvent) {
        ((Stage) getScene().getWindow()).setFullScreen(true);
        screenButton.setOnMouseClicked(this::exitFullscreen);
        ObservableList<String> styleClass = screenButton.getStyleClass();
        styleClass.remove("maximize");
        styleClass.add("minimize");
    }

    public void exitFullscreen(MouseEvent mouseEvent){
        ((Stage) getScene().getWindow()).setFullScreen(false);
        screenButton.setOnMouseClicked(this::enterFullscreen);
        ObservableList<String> styleClass = screenButton.getStyleClass();
        styleClass.remove("minimize");
        styleClass.add("maximize");
    }
}

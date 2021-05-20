package client.ui.controller;

import client.game.GameManager;
import client.game.GameState;
import client.ui.customcontrol.GameBoxButton;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import client.model.Board;
import client.model.Box;
import client.model.BoxValue;

public class GameController extends Controller{

    @FXML
    private GridPane mainContainer;
    @FXML
    private Label playerLabel;
    @FXML
    private Label rivalLabel;
    @FXML
    private GridPane rivalBoardContainer;
    @FXML
    private GridPane playerBoardContainer;

    private GameBoxButton[][] playerBoxButtons, rivalBoxButtons;


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

    }

    /*
    * Se notifica al GameManager la casilla a empezar partida
    * todos los bottones cambian de listener para empezar partida
    * a listener para descubrir/abanderar casillas
    */
    public void startMatch(MouseEvent mouseEvent){
        GameBoxButton source = (GameBoxButton) mouseEvent.getSource();

        getGameManager().startMatchAt(source.getColumn(), source.getRow());



        for (int rowIndex = 0; rowIndex < playerBoxButtons.length; rowIndex++) {
            for (int colIndex = 0; colIndex < playerBoxButtons[0].length; colIndex++) {

                playerBoxButtons[rowIndex][colIndex]
                        .setBox(getGameManager().getPlayerBoard().getBoxAt(colIndex, rowIndex));
                playerBoxButtons[rowIndex][colIndex]
                        .setOnMouseClicked(this::handleBoxClicked);


            }
        }

        handleBoxClicked(mouseEvent);


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

            if(getGameManager().getRival() != null) {
                Platform.runLater(() -> {
                    rivalLabel.setText(getGameManager().getRival().getNickname());
                });
            }


            for (int boardIndex = 0; boardIndex < allBoards.length; boardIndex++) {
                for (int rowIndex = 0; rowIndex < getGameManager().getRows(); rowIndex++) {
                    for (int colIndex = 0; colIndex < getGameManager().getColumns(); colIndex++) {


                        Box box = allBoards[boardIndex].getBoxAt(colIndex, rowIndex);
                        GameBoxButton gameBoxButton = allButtons[boardIndex][rowIndex][colIndex];


                        switch (box.getStatus()) {
                            case FLAGGED:
                                //bugfix ui_1: busca si la casilla ya tenia la clase asi no tiene la clase dos veces
                                if (!gameBoxButton.getStyleClass().contains("flagged"))
                                    gameBoxButton.getStyleClass().add("flagged");
                                break;
                            case VISIBLE:
                                String value = box.getValue().getValue();

                                gameBoxButton.getStyleClass().remove("flagged");
                                gameBoxButton.getStyleClass().add("value-" + value);
                                gameBoxButton.setDisable(true);

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
                                gameBoxButton.getStyleClass().remove("flagged");
                                gameBoxButton.setText("");
                                break;

                        }

                        if(getGameManager().getGameState() != GameState.UNDEFINED){
                            keepRunning = false;
                        }
                    }
                }
            }

            Thread.sleep(100);
        }while (keepRunning);
        System.out.println(getGameManager().getGameState().toString());

        Alert alert = new Alert(Alert.AlertType.NONE);
        switch (getGameManager().getGameState()){
            case PLAYER_LOST_BY_MINE:
                alert.setTitle("Derrota");
                alert.setContentText("Has activado una mina");
                break;
            case PLAYER_WON_BY_FLAG:
                alert.setTitle("Victoria");
                alert.setContentText("Has desactivado todas las minas");
                break;
            case RIVAL_WON_BY_FLAG:
                alert.setTitle("Derrota");
                alert.setContentText("El rival desactivo todas las minas");
                break;
            case RIVAL_LOST_BY_FLAG:
                alert.setTitle("Victoria");
                alert.setContentText("El rival activo una mina");
                break;
        }
        alert.show();
    }

    /*
    * Decide que hacer hacer con clic primario o secundario,
    * si es primario se revela el valor
    * si es secundario se alterna su estado de bandera
    * */

    public void handleBoxClicked(MouseEvent mouseEvent){
        GameBoxButton boxButtonSource = (GameBoxButton) mouseEvent.getSource();
        int boxColumn = boxButtonSource.getColumn();
        int boxRow = boxButtonSource.getRow();

        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            getGameManager().boxActived(boxColumn, boxRow);

        }else {

            getGameManager().toggleFlagStatus(boxColumn, boxRow);

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

    public void updateLabels() {
        rivalLabel.setText(getGameManager().getRival().getNickname());
        playerLabel.setText(getGameManager().getPlayer().getNickname());
    }
}

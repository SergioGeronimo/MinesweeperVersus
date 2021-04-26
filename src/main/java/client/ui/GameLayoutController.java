package client.ui;

import client.game.GameManager;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

public class GameLayoutController {


    @FXML
    private GridPane rivalBoardContainer;
    @FXML
    private GridPane rivalMatchStats;
    @FXML
    private GridPane playerBoardContainer;
    @FXML
    private GridPane playerMatchStats;

    private GameBoxButton[][] playerBoxArray, rivalBoxArray;

    GameManager gameManager;


    public void setMatchReady(MouseEvent mouseEvent) {
        gameManager = new GameManager(10, 10, 10);
        gameManager.setMatchReady();

        playerBoxArray = new GameBoxButton[gameManager.getRows()][gameManager.getColumns()];
        rivalBoxArray = new GameBoxButton[gameManager.getRows()][gameManager.getColumns()];

        fillBoardLayout(gameManager.getColumns(), gameManager.getRows(), playerBoardContainer, playerBoxArray);
        fillBoardLayout(gameManager.getColumns(), gameManager.getRows(), rivalBoardContainer, rivalBoxArray);

        //se remueve el listener, bugfix 1: el tablero se reinicia cada vez que se hace clic
        ((GridPane) mouseEvent.getSource()).setOnMouseClicked(null);
    }

    /*
     * Se notifica al GameManager que la casilla a empezar partida
     * todos los bottones cambian de listener para empezar partida
     * a listener para descubrir/abanderar casillas
     */
    public void startMatch(MouseEvent mouseEvent){
        GameBoxButton source = (GameBoxButton) mouseEvent.getSource();
        gameManager.startMatchAt(source.getColumn(), source.getRow());


        for (int rowIndex = 0; rowIndex < playerBoxArray.length; rowIndex++) {
            for (int colIndex = 0; colIndex < playerBoxArray[colIndex].length; colIndex++) {
                GameBoxButton boxButton = new GameBoxButton(colIndex + "," + rowIndex, colIndex, rowIndex);
                playerBoxArray[rowIndex][colIndex].setOnMouseClicked(this::manageBoxClicked);
            }
        }
    }

    public void manageBoxClicked(MouseEvent mouseEvent){

    }

    // create the board array and fill player gridpane with buttons
    public void fillBoardLayout(int columns, int rows, GridPane boardContainer, GameBoxButton[][] boxButtonsArray){

        boardContainer.getChildren().clear();

        ObservableList<ColumnConstraints> containerColumnConstraints = boardContainer.getColumnConstraints();
        containerColumnConstraints.clear();
        for (int colCount = 0; colCount < columns; colCount++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setHgrow(Priority.ALWAYS);
            containerColumnConstraints.add(columnConstraints);
        }

        ObservableList<RowConstraints> containerRowConstraints = boardContainer.getRowConstraints();
        containerRowConstraints.clear();
        for (int rowCount = 0; rowCount < rows; rowCount++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setVgrow(Priority.ALWAYS);
            containerRowConstraints.add(rowConstraints);
        }

        for (int colCount = 0; colCount < columns; colCount++) {
            for (int rowCount = 0; rowCount < rows; rowCount++) {
                GameBoxButton boxButton = new GameBoxButton(rowCount + "," + colCount, rowCount, colCount);
                boxButtonsArray[rowCount][colCount] = boxButton;
                boxButton.setOnMouseClicked(this::startMatch);
                boardContainer.add(boxButton, colCount, rowCount);
            }
        }

    }
}

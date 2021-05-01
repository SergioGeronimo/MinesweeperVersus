package client.ui;

import client.game.GameManager;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import model.board.Board;
import model.board.Box;
import model.board.BoxValue;

import java.util.Arrays;

public class GameLayoutController {


    @FXML
    private GridPane rivalBoardContainer;
    @FXML
    private GridPane rivalMatchStats;
    @FXML
    private GridPane playerBoardContainer;
    @FXML
    private GridPane playerMatchStats;

    private GameBoxButton[][] playerBoxButtons, rivalBoxButtons;

    GameManager gameManager;


    public void setMatchReady(MouseEvent mouseEvent) {
        gameManager = new GameManager(10, 8, 5);
        gameManager.setMatchReady();

        playerBoxButtons = new GameBoxButton[gameManager.getRows()][gameManager.getColumns()];
        rivalBoxButtons = new GameBoxButton[gameManager.getRows()][gameManager.getColumns()];

        fillGridPaneWithButtons(
                gameManager.getColumns(),
                gameManager.getRows(),
                playerBoardContainer,
                playerBoxButtons,
                false);

        fillGridPaneWithButtons(
                gameManager.getColumns(),
                gameManager.getRows(),
                rivalBoardContainer,
                rivalBoxButtons,
                false);


        //se remueve el listener, bugfix 1: el tablero se reinicia cada vez que se hace clic
        ((GridPane) mouseEvent.getSource()).setOnMouseClicked(null);
    }

    /*
     * Se notifica al GameManager la casilla a empezar partida
     * todos los bottones cambian de listener para empezar partida
     * a listener para descubrir/abanderar casillas
     */
    public void startMatch(MouseEvent mouseEvent){
        GameBoxButton source = (GameBoxButton) mouseEvent.getSource();

        gameManager.startMatchAt(source.getColumn(), source.getRow());



        for (int rowIndex = 0; rowIndex < playerBoxButtons.length; rowIndex++) {
            for (int colIndex = 0; colIndex < playerBoxButtons[0].length; colIndex++) {

                playerBoxButtons[rowIndex][colIndex].setBox(gameManager.getPlayerBoard().getBoxAt(colIndex, rowIndex));
                playerBoxButtons[rowIndex][colIndex].setOnMouseClicked(this::handleBoxClicked);


            }
        }

        handleBoxClicked(mouseEvent);
    }

    public void updateAllBoxText(){
        Board playerBoard = gameManager.getPlayerBoard();

        for (int rowIndex = 0; rowIndex < playerBoard.getRows(); rowIndex++) {
            for (int colIndex = 0; colIndex < playerBoard.getColumns(); colIndex++) {


                Box box = gameManager.getPlayerBoard().getBoxAt(colIndex, rowIndex);
                GameBoxButton gameBoxButton = playerBoxButtons[rowIndex][colIndex];

                switch (box.getStatus()){
                    case FLAGGED:
                        gameBoxButton.setText("F");
                        break;
                    case VISIBLE:
                        gameBoxButton.setText(box.getValue().getValue()+"");
                        break;
                    case HIDDEN:
                        gameBoxButton.setText("");
                        break;

                }


            }
        }

    }



    public void handleBoxClicked(MouseEvent mouseEvent){
        GameBoxButton boxButtonSource = (GameBoxButton) mouseEvent.getSource();
        int boxColumn = boxButtonSource.getColumn();
        int boxRow = boxButtonSource.getRow();

        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            gameManager.revealPlayerBoxValue(boxColumn, boxRow);

        }else {

            gameManager.toggleFlagStatus(boxColumn, boxRow);

        }

        updateAllBoxText();

    }


    public void fillGridPaneWithButtons(int columns, int rows, GridPane boardContainer, GameBoxButton[][] boxButtonsArray, boolean isDisabled){

        boardContainer.getChildren().clear();
        boardContainer.setGridLinesVisible(true);


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

        for (int rowCount = 0; rowCount < rows; rowCount++) {
            for (int colCount = 0; colCount < columns; colCount++) {

                GameBoxButton boxButton = new GameBoxButton(colCount, rowCount);
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
}

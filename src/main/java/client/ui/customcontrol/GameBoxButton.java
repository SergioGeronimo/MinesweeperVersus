package client.ui.customcontrol;

import javafx.scene.Node;
import javafx.scene.control.Button;
import client.model.Box;

public class GameBoxButton extends Button {
    private int column, row;
    private Box box;


    public GameBoxButton(int column, int row) {
        this.column = column;
        this.row = row;
    }
    //Getters y setters basicos y comunes
    public Box getBox() {
        return box;
    }

    public void setBox(Box box) {
        this.box = box;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }
}

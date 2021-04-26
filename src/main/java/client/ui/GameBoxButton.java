package client.ui;

import javafx.scene.Node;
import javafx.scene.control.Button;

public class GameBoxButton extends Button {
    private int column, row;


    public GameBoxButton(int column, int row) {
        this.column = column;
        this.row = row;
    }

    public GameBoxButton(String s, int column, int row) {
        super(s);
        this.column = column;
        this.row = row;
    }

    public GameBoxButton(String s, Node node, int column, int row) {
        super(s, node);
        this.column = column;
        this.row = row;
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

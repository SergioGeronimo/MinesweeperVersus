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

    public GameBoxButton(int column, int row, Box box) {
        this.column = column;
        this.row = row;
        this.box = box;
    }

    public GameBoxButton(String s, int column, int row, Box box) {
        super(s);
        this.column = column;
        this.row = row;
        this.box = box;
    }

    public GameBoxButton(String s, Node node, int column, int row, Box box) {
        super(s, node);
        this.column = column;
        this.row = row;
        this.box = box;
    }

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

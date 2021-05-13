package client.model;

import java.io.Serializable;

public class Box implements Serializable {
    int column, row;
    private BoxValue value;
    private BoxStatus status;


    public Box(BoxValue value) {
        this.value = value;
        this.status = BoxStatus.VISIBLE;
    }

    public Box(BoxValue value, BoxStatus status) {
        this.value = value;
        this.status = status;
    }

    public Box(int column, int row) {
        this.column = column;
        this.row = row;
        this.status = BoxStatus.HIDDEN;
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

    public BoxValue getValue() {
        return value;
    }

    public void setValue(BoxValue value) {
        this.value = value;
    }

    public BoxStatus getStatus() {
        return status;
    }

    public void setStatus(BoxStatus status) {
        this.status = status;
    }



}

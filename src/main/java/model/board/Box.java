package model.board;

public class Box {
    private BoxValue value;
    private BoxStatus status;

    public Box() {
    }

    public Box(BoxValue value, BoxStatus status) {
        this.value = value;
        this.status = status;
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

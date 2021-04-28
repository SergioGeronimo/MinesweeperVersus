package model.board;

public enum BoxValue{
    START(-2),
    MINE(-1),
    NONE_NEAR(0),
    ONE_NEAR(1),
    TWO_NEAR(2),
    THREE_NEAR(3),
    FOUR_NEAR(4),
    FIVE_NEAR(5),
    SIX_NEAR(6),
    SEVEN_NEAR(7),
    EIGHT_NEAR(8);

    private final int value;

    private BoxValue(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

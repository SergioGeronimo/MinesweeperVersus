package model.board;

public enum BoxValue{
    START("s"),
    MINE("x"),
    NONE_NEAR("0"),
    ONE_NEAR("1"),
    TWO_NEAR("2"),
    THREE_NEAR("3"),
    FOUR_NEAR("4"),
    FIVE_NEAR("5"),
    SIX_NEAR("6"),
    SEVEN_NEAR("7"),
    EIGHT_NEAR("8");

    private final String value;

    private BoxValue(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

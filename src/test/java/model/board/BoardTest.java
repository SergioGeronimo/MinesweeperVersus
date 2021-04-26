package model.board;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private Board boardTested;
    int columns = 10;
    int rows = 10;
    int mines = 25;

    @BeforeEach
    public void setUp() throws Exception{

        boardTested = new Board(columns, rows, mines);
    }

    @Test
    public void canGenerateEmptyBoardGrid() throws Exception{
        Box[][] expected = new Box[rows][columns];
        Box[][] actual = boardTested.generateEmptyGrid();

        assertArrayEquals(expected, actual);
    }

    @Test
    public void fillMines() throws Exception{
        int expectedMines = mines;
        int actualMines = 0;
        boardTested.generateEmptyGrid();
        Box[][] generateRandomPlacedMines = boardTested.generateMines(columns/2, rows/2);

        for (Box[] column: generateRandomPlacedMines) {
            for (Box box: column){
                if (box.getValue() == BoxValue.MINE){
                    actualMines++;
                }

            }
        }

        printBoard(generateRandomPlacedMines);
        assertEquals(expectedMines, actualMines);
    }

    @Test
    public void canFillIndicators() throws Exception{
        boardTested.generateEmptyGrid();

        boardTested.generateMines(columns/2, rows/2);
        Box[][] indicators = boardTested.fillNearIndicators();
        printBoard(indicators);
        // determinar una forma de testear la generacion
    }

    private void printBoard(Box[][] board){
        for (Box[] column: board) {
            for (Box box: column){
                switch (box.getValue()){
                    case START:
                        System.out.print("S\t");
                        break;
                    case EMPTY:
                        System.out.print("0\t");
                        break;
                    case ONE_NEAR:
                        System.out.print("1\t");
                        break;
                    case TWO_NEAR:
                        System.out.print("2\t");
                        break;
                    case THREE_NEAR:
                        System.out.print("3\t");
                        break;
                    case FOUR_NEAR:
                        System.out.print("4\t");
                        break;
                    case FIVE_NEAR:
                        System.out.print("5\t");
                        break;
                    case SIX_NEAR:
                        System.out.print("6\t");
                        break;
                    case SEVEN_NEAR:
                        System.out.print("7\t");
                        break;
                    case EIGHT_NEAR:
                        System.out.print("8\t");
                        break;
                    case MINE:
                        System.out.print("X\t");
                        break;
                }

            }
            System.out.println();
        }
    }

}
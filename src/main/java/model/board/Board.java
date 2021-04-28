package model.board;

import java.util.Random;

public class Board {
    private int columns;
    private int rows;
    private int mines;


    private int flaggedMines;
    private Box[][] grid;

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getMines() {
        return mines;
    }

    public void setMines(int mines) {
        this.mines = mines;
    }

    public Box[][] getGrid() {
        return grid;
    }

    public void setGrid(Box[][] grid) {
        this.grid = grid;
    }

    public int getFlaggedMines() {
        return flaggedMines;
    }

    public void setFlaggedMines(int flaggedMines) {
        this.flaggedMines = flaggedMines;
    }

    public Box getBoxAt(int columnIndex, int rowIndex) throws ArrayIndexOutOfBoundsException{
        return grid[rowIndex][columnIndex];
    }

    public Board(int columns, int rows, int mines) {
        this.columns = columns;
        this.rows = rows;
        this.mines = mines;
    }

    public Box[][] generateEmptyGrid() {
        this.grid = new Box[rows][columns];
        return this.grid;
    }

    /*
     *  Este metodo genera el mapa con minas puestas pseudoaleatoriamente,
     *  sin embargo, evita la casilla donde inicia el jugador, asi no hay
     *  probabilidad de perder al instante
     *  @params columna y renglon de casilla a evitar (inicial)
     *  @returns el tablero generado con minas
     */
    public Box[][] generateMines(int avoidedColumn, int avoidedRow) {
        Random random = new Random();
        int boxCount = this.columns * this.rows;
        int avoidedIndex = avoidedRow * grid[0].length + avoidedColumn;
        //System.out.println("avoidedColumn = " + avoidedColumn + " avoidedRow = " + avoidedRow + "avoidedIndex = " + avoidedIndex);

        boolean[] minesLocation = new boolean[boxCount];
        int minesLeftToPlace = mines;
        while (minesLeftToPlace != 0){
            int mineIndex = random.nextInt(this.columns * this.rows);
            if(!minesLocation[mineIndex] && mineIndex != avoidedIndex){
                minesLocation[mineIndex] = true;
                minesLeftToPlace--;
            }
        }

        int mineIndex = 0;

        for (int columnIndex = 0; columnIndex < grid.length; columnIndex++) {
            for(int rowIndex = 0; rowIndex < grid[columnIndex].length; rowIndex++){
                if(minesLocation[mineIndex]){
                    grid[columnIndex][rowIndex] = new Box(BoxValue.MINE, BoxStatus.HIDDEN);
                }else if(mineIndex == avoidedIndex){
                    grid[columnIndex][rowIndex] = new Box(BoxValue.START, BoxStatus.HIDDEN);
                }
                else{
                    grid[columnIndex][rowIndex] = new Box(BoxValue.NONE_NEAR, BoxStatus.HIDDEN);
                }
                mineIndex++;

            }
        }
        return this.grid;
    }

    public Box[][] fillNearIndicators(){
        for (int columnIndex = 0; columnIndex < grid.length; columnIndex++) {
            for(int rowIndex = 0; rowIndex < grid[columnIndex].length; rowIndex++){
                int minesNear = 0;

                if(grid[columnIndex][rowIndex].getValue() != BoxValue.MINE) {

                    for (short columnOffset = -1; columnOffset < 2; columnOffset++) {
                        for (short rowOffset = -1; rowOffset < 2; rowOffset++) {
                            try {
                                if (grid[columnIndex + columnOffset][rowIndex + rowOffset].getValue() == BoxValue.MINE) {
                                    minesNear++;
                                }

                            } catch (ArrayIndexOutOfBoundsException ignored) {

                            }
                        }
                    }
                    switch (minesNear) {
                        case 1:
                            grid[columnIndex][rowIndex].setValue(BoxValue.ONE_NEAR);
                            break;
                        case 2:
                            grid[columnIndex][rowIndex].setValue(BoxValue.TWO_NEAR);
                            break;
                        case 3:
                            grid[columnIndex][rowIndex].setValue(BoxValue.THREE_NEAR);
                            break;
                        case 4:
                            grid[columnIndex][rowIndex].setValue(BoxValue.FOUR_NEAR);
                            break;
                        case 5:
                            grid[columnIndex][rowIndex].setValue(BoxValue.FIVE_NEAR);
                            break;
                        case 6:
                            grid[columnIndex][rowIndex].setValue(BoxValue.SIX_NEAR);
                            break;
                        case 7:
                            grid[columnIndex][rowIndex].setValue(BoxValue.SEVEN_NEAR);
                            break;
                        case 8:
                            grid[columnIndex][rowIndex].setValue(BoxValue.EIGHT_NEAR);
                            break;
                    }

                }
            }
        }

        return this.grid;
    }

    public void setBoxStatus(int column, int row, BoxStatus boxStatus) {
        grid[row][column].setStatus(boxStatus);
    }
}

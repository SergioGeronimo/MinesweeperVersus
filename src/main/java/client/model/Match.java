package client.model;

import client.game.GameState;

import java.io.Serializable;

public class Match implements Serializable {

    int matchID;
    Player playerA, playerB;
    int columns, rows, mines;
    GameState gameState;
    boolean Changed;
    Box lastBoxPlayerA, lastBoxPlayerB;
    Board boardPlayerA, boardPlayerB;


    public Match(int matchID, int columns, int rows, int mines) {
        this.matchID = matchID;
        this.columns = columns;
        this.rows = rows;
        this.mines = mines;
        this.gameState = GameState.UNDEFINED;
    }

    public Match(
            int matchID,
            Player playerA,
            Player playerB,
            int columns,
            int rows,
            int mines) {

        this.matchID = matchID;
        this.playerA = playerA;
        this.playerB = playerB;
        this.columns = columns;
        this.rows = rows;
        this.mines = mines;
        this.gameState = GameState.UNDEFINED;

    }


    public int getMatchID() {
        return matchID;
    }

    public void setMatchID(int matchID) {
        this.matchID = matchID;
    }

    public boolean isChanged() {
        return Changed;
    }

    public void setChanged(boolean changed) {
        Changed = changed;
    }

    public Player getPlayerA() {
        return playerA;
    }

    public void setPlayerA(Player playerA) {
        this.playerA = playerA;
    }

    public Player getPlayerB() {
        return playerB;
    }

    public void setPlayerB(Player playerB) {
        this.playerB = playerB;
    }

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

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public Box getLastBoxPlayerA() {
        return lastBoxPlayerA;
    }

    public void setLastBoxPlayerA(Box lastBoxPlayerA) {
        this.lastBoxPlayerA = lastBoxPlayerA;
    }

    public Box getLastBoxPlayerB() {
        return lastBoxPlayerB;
    }

    public void setLastBoxPlayerB(Box lastBoxPlayerB) {
        this.lastBoxPlayerB = lastBoxPlayerB;
    }

    public Board getBoardPlayerA() {
        return boardPlayerA;
    }

    public void setBoardPlayerA(Board boardPlayerA) {
        this.boardPlayerA = boardPlayerA;
    }

    public Board getBoardPlayerB() {
        return boardPlayerB;
    }

    public void setBoardPlayerB(Board boardPlayerB) {
        this.boardPlayerB = boardPlayerB;
    }
}

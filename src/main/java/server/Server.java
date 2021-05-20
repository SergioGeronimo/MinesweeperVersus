package server;

import client.game.GameState;
import client.model.Board;
import client.model.Box;
import client.model.Match;
import client.model.Player;
import rmiinterface.ServerRemote;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Server extends UnicastRemoteObject implements ServerRemote {
    private static final long serialVersionUID = 1L;
    private static final String  HOSTNAME = "192.168.0.6";
    private static final int MAX_MATCHES = 100;

    Hashtable<String, Player> playerHashtable;
    Hashtable<Integer, Match> matchHashtable;


    @Override
    public boolean addClient(Player player) throws RemoteException {

        if (playerHashtable.containsKey(player.getNickname())){
            return false;
        }else {
            playerHashtable.put(player.getNickname(), player);
            return true;
        }
    }


    /*
    * Añade a un jugador a una partida,
    * @returns el id de la partida
    * */
    @Override
    public int joinPlayerToMatch(Player player) {
        //id de la partida a unir, AtomicInteger permite cambios dentro de expresiones lambda
        AtomicInteger id = new AtomicInteger();
        //bandera si el jugador se unio a partida, tambien permite cambios dentro de expresiones lamba
        AtomicBoolean playerJoined = new AtomicBoolean(false);

        //si no hay partidas, se crea una
        if (matchHashtable.isEmpty()){
            id.set(createMatch());
        }


        //checa si hay partidas disponibles, pone al jugador en la primera que encuentre
        //como jugador A o B
        matchHashtable.forEach( (key , value) ->{

            if (!playerJoined.get()) {
                if (value.getPlayerA() == null) {
                    value.setPlayerA(player);
                    id.set(value.getMatchID());
                    playerJoined.set(true);

                } else if (value.getPlayerB() == null) {
                    value.setPlayerB(player);
                    id.set(value.getMatchID());
                    playerJoined.set(true);
                }
            }

        });


        //si no se encuentra partida, se crea una y lo añade
        if(!playerJoined.get()){
            id.set(createMatch());
            matchHashtable.get(id.get()).setPlayerA(player);
        }

        System.err.println("User [" + player.getNickname() + "] in game #" + id.get());
        return id.get();
    }

    /*
    * quita al jugador de una partida en especifico
    * */
    @Override
    public void detachPlayerToMatch(int matchId, String nickname) throws RemoteException {
        Match match = matchHashtable.get(matchId);

        if(match.getPlayerA().getNickname().equals(nickname)){
            match.setPlayerA(null);
        }else {
            match.setPlayerB(null);
        }

    }

    /*
    * Busca si el jugador esta en partida y lo quita
    * si el jugador no se encuentra o es null nada pasa
    * */
    @Override
    public void detachPlayer(Player player) throws RemoteException {
        if (player != null) {
            matchHashtable.forEach((key, value) -> {
                if (player.getNickname().equals(value.getPlayerA().getNickname())) {
                    value.setPlayerA(null);
                } else {
                    value.setPlayerB(null);
                }
            });
        }
    }

    @Override
    public boolean isMatchReady(int matchID) throws RemoteException {
        boolean playerAReady = matchHashtable.get(matchID).getPlayerA() != null;
        boolean playerBReady = matchHashtable.get(matchID).getPlayerB() != null;
        return playerAReady && playerBReady;
    }

    @Override
    public Board getRivalBoard(int matchID, boolean isPlayerA) throws RemoteException {

        Match match = matchHashtable.get(matchID);

        if (isPlayerA){
            return match.getBoardPlayerB();
        }else {
            return match.getBoardPlayerA();
        }
    }

    @Override
    public void sendBoard(int matchId, boolean isPLayerA, Board board) throws RemoteException {

        Match match = matchHashtable.get(matchId);

        if(isPLayerA){
            match.setBoardPlayerA(board);
        }else {
            match.setBoardPlayerB(board);
        }

    }

    @Override
    public Box getRivalBox(int matchID, boolean isPLayerA) throws RemoteException {
        Match match = matchHashtable.get(matchID);

        if (isPLayerA){
            return match.getLastBoxPlayerB();
        }else {
            return match.getLastBoxPlayerA();
        }
    }

    @Override
    public void sendBox(int matchId, boolean isPlayerA, Box box) throws RemoteException {
        Match match = matchHashtable.get(matchId);
        if (isPlayerA){
            match.setLastBoxPlayerA(box);
        }else {
            match.setLastBoxPlayerB(box);
        }
    }

    @Override
    public boolean askIsPlayerA(int matchID, String nickname) throws RemoteException {
        Match match = matchHashtable.get(matchID);
        return match.getPlayerA().getNickname().equals(nickname);
    }

    public int createMatch(){

        int id = 0;

        do {
            id = new Random().nextInt(MAX_MATCHES);

        }while (matchHashtable.containsKey(id));

        Match createdMatch = new Match(id, 10, 10, 10);
        matchHashtable.put(id, createdMatch);

        return id;

    }

    @Override
    public GameState getGameState(int matchID) throws RemoteException {
        return matchHashtable.get(matchID).getGameState();
    }

    protected Server() throws RemoteException{
        super();
        matchHashtable = new Hashtable<>();
        playerHashtable = new Hashtable<>();
    }

    @Override
    public void setGameState(int matchID, GameState gameState) throws RemoteException {
        matchHashtable.get(matchID).setGameState(gameState);
    }

    @Override
    public Player getRival(int matchID, boolean isPlayerA) throws RemoteException {
        if (isPlayerA){
            return matchHashtable.get(matchID).getPlayerB();
        }else {
            return matchHashtable.get(matchID).getPlayerA();
        }
    }

    public static void main(String[] args) {
        /*
        if(System.getSecurityManager() == null){
            System.setSecurityManager(new SecurityManager());
        }*/


        try {

            System.setProperty("java.rmi.server.hostname", HOSTNAME);
            Registry registry = LocateRegistry.createRegistry(4444);
            Naming.rebind("rmi://localhost:4444/MinesweeperServer", new Server());
            System.err.println("Server launched");
        } catch (RemoteException | MalformedURLException e) {
            e.printStackTrace();
        }

    }
}

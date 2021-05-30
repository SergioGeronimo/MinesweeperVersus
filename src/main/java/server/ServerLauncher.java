package server;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerLauncher {
    private static String hostname;
    private static final int PORT = 4444;

    public static void main(String[] args) {
        try {
            hostname = InetAddress.getLocalHost().getHostAddress();
            System.out.println("Server IPv4:\t" + hostname);

            System.setProperty("java.rmi.server.hostname", hostname);
            Registry registry = LocateRegistry.createRegistry(PORT);
            Naming.rebind("//"+ hostname + ":" + PORT +"/MinesweeperServer", new Server());
            System.out.println("Server launched");
        } catch (RemoteException | MalformedURLException | UnknownHostException e) {
            e.printStackTrace();
        }

    }
}

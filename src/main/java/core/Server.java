package core;

import utils.ConsolePrinter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    public static final boolean ALLOW_PRINT = true;

    private final String
            HOST = "localhost",
            DB = "multiplayer",
            USER = "root",
            PASS = "root";

    private final int DB_PORT = 3306;
    private final int SERVER_PORT = 26265;

    private DBConnectionManager manager;
    private boolean running;
    private List<ConnectionThread> connections;

    public Server() {
        this.running = false;
        this.connections = new ArrayList<>();
        this.manager = new DBConnectionManager(
                new ConnectionData(HOST, DB, USER, PASS, DB_PORT));
    }

    public void start() {

        ConsolePrinter.print(
                ConsolePrinter.PrintStatus.OK,
                true,
                "Server starting..."
        );

        this.running = true;
        this.run();
    }

    // This method is always running on the server.
    // It's purpose is to accept connections from clients.
    private void run() {

        ServerSocket serverSocket = null;
        Socket client;

        try { serverSocket = new ServerSocket(SERVER_PORT); }
        catch (IOException e) { e.printStackTrace(); }

        if(serverSocket == null) {
            throw new IllegalStateException("Server socket was null.");
        }

        ConsolePrinter.print(
                ConsolePrinter.PrintStatus.OK,
                true,
                "Waiting connections..."
        );

        while(running) {
            try {
                client = serverSocket.accept();
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to accept connection.");
            }

            // TODO: we need a way of removing connections from the list.
            this.connections.add(new ConnectionThread(client, this.manager));

            ConsolePrinter.print(
                    ConsolePrinter.PrintStatus.OK,
                    true,
                    "New connection: " + client.toString()
            );
        }
    }

}

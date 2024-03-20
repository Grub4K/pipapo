package com.grub4k.pipapo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Server {
    private ServerSocket serverSocket;
    private ArrayList<Thread> threads;
    private ArrayList<ServerGame> games;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        threads = new ArrayList<>();
        games = new ArrayList<>();
    }

    public void run() throws IOException {
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println(String.format("%s: new connection", socket.getInetAddress().toString()));
            ServerGame game = new ServerGame(socket);
            games.add(game);
            Thread thread = new Thread(game);
            threads.add(thread);
            thread.start();

            // Throw out old games and threads
            games = games
                .parallelStream()
                .filter((x) -> !x.isClosed())
                .collect(Collectors.toCollection(ArrayList::new));
            threads = threads
                .parallelStream()
                .filter((x) -> !x.isAlive())
                .collect(Collectors.toCollection(ArrayList::new));
        }
    }

    public void close() throws IOException {
        serverSocket.close();
        for (ServerGame game : games) {
            game.close();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }
}

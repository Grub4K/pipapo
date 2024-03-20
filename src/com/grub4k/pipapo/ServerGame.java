package com.grub4k.pipapo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Runnable;
import java.net.Socket;
import java.util.Random;

public class ServerGame implements Runnable {
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private boolean closed;
    private static Random random = new Random();
    private static Logic.Choice[] choices = Logic.Choice.values();

    public ServerGame(Socket socket) throws IOException {
        this.socket = socket;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
        closed = false;
    }

    @Override
    public void run() {
        int winsUser = 0;
        int winsServer = 0;

        while (true) {
            Logic.Choice choiceServer = choices[random.nextInt(choices.length)];
            Logic.Choice choiceUser = null;
            while (choiceUser == null) {
                if (closed) {
                    close();
                    return;
                }
                String input;
                try {
                    input = reader.readLine();
                } catch (IOException e) {
                    System.err.println(e);
                    close();
                    return;
                }

                if (closed || input == null || "end".equals(input)) {
                    close();
                    return;
                }
                try {
                    choiceUser = Logic.fromString(input);
                } catch (Exception e) {
                    writer.println("error: Invalid choice!");
                }
            }
            System.out.println(String.format("server: %s, client: %s", choiceServer, choiceUser));

            int result = Logic.compare(choiceUser, choiceServer);

            String message;
            if (result == 0) {
                message = "draw";
            } else if (result == 1) {
                message = "win";
                winsUser += 1;
            } else {
                message = "loose";
                winsServer += 1;
            }

            writer.println(String.format("%s:%s|%d:%d", message, choiceServer.name().toLowerCase(), winsUser, winsServer));
            if (winsUser == 3 || winsServer == 3) {
                close();
                return;
            }
        }
    }

    public boolean isClosed() {
        return closed;
    }

    public void close() {
        if (closed) {
            return;
        }
        writer.close();
        try {
            reader.close();
            socket.close();
        } catch (IOException e) {
            System.err.println(e);
        }
        closed = true;
    }
}

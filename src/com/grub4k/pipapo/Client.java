package com.grub4k.pipapo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

public class Client {
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private BufferedReader stdin;
    private boolean closed;
    private static String options = String.join(", ", Arrays.stream(Logic.Choice.values()).map(x -> x.toString().toLowerCase()).toArray(String[]::new));

    public Client(String ip, int port) throws UnknownHostException, IOException {
        this.socket = new Socket(ip, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(this.socket.getOutputStream(), true);
        stdin = new BufferedReader(new InputStreamReader(System.in));
        closed = false;
    }

    public void run() throws IOException {
        String input;

        while (true) {
            System.out.print(String.format("Enter your choice or \"end\" to stop (%s): ", options));
            input = stdin.readLine();
            if (input == null || input.isEmpty()) {
                writer.println("end");
                close();
                return;
            }
            writer.println(input);

            try {
                input = reader.readLine();
            } catch (IOException e) {
                System.err.println(e);
                close();
                return;
            }
            if (input == null) {
                close();
                return;
            }
            if (input.startsWith("error: ")) {
                System.err.println(input.substring("error: ".length()));
                continue;
            }

            String[] parts = input.split("[:|]");
            if (parts.length != 4) {
                System.err.println(String.format("Unexpected response format: %s", input));
                close();
                return;
            }

            int winsUser;
            try {
                winsUser = Integer.parseInt(parts[2]);
            } catch (Exception e) {
                System.err.println(e);
                close();
                return;
            }
            int winsServer;
            try {
                winsServer = Integer.parseInt(parts[3]);
            } catch (Exception e) {
                System.err.println(e);
                close();
                return;
            }
            System.out.println(String.format(
                "Server chose %s! %s! (You: %d, Server: %d)",
                parts[1], parts[0], winsUser, winsServer
            ));

            if (winsUser == 3 || winsServer == 3) {
                System.out.println(String.format("You %s the game!", winsUser == 3 ? "win" : "loose"));
                close();
                return;
            }
        }
    }

    public void close() {
        if (closed) {
            return;
        }
        writer.close();
        try {
            reader.close();
            socket.close();
            stdin.close();
        } catch (IOException e) {
            System.err.println(e);
        }
        closed = true;
    }
}

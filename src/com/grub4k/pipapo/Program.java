package com.grub4k.pipapo;

import java.io.IOException;

public class Program {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            usage();
        } else if (args[0].equals("server")) {
            int port = getPort(args, 1);
            Server server = new Server(port);
            System.out.println(String.format("Serving at port %d", port));
            server.run();
        } else if (args[0].equals("client")) {
            if (args.length < 2) {
                System.err.println("`ip` is a required parameter");
                usage();
            }
            int port = getPort(args, 2);
            Client client = new Client(args[1], port);
            client.run();
        } else {
            System.err.println(String.format("Invalid choice: `%s`, choose between `server` and `client`", args[0]));
            usage();
        }
    }

    public static int getPort(String[] args, int index) {
        if (args.length <= index) {
            return 12345;
        }
        try {
            return Integer.parseInt(args[index]);
        } catch (NumberFormatException e) {}
        System.err.println("`port` needs to be a valid port number");
        usage();
        return 0;
    }
    public static void usage() {
        System.err.println("Usage: pipapo server [port]\n       pipapo client <ip> [port]");
        System.exit(1);
    }
}

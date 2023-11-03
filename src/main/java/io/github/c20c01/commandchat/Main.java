package io.github.c20c01.commandchat;

import io.github.c20c01.commandchat.client.Client;
import io.github.c20c01.commandchat.server.Server;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java -jar CommandChat.jar <server|client>");
        } else if (args[0].equals("server")) {
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.print("Server port : ");
                int port = scanner.nextInt();
                new Server(port);
            }
        } else if (args[0].equals("client")) {
            new Client();
        }
    }
}

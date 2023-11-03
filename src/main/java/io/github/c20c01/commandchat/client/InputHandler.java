package io.github.c20c01.commandchat.client;

import io.github.c20c01.commandchat.pkg.PUserList;
import io.github.c20c01.commandchat.util.LoopThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class InputHandler implements Runnable {
    private final BufferedReader READER;
    private final Client CLIENT;
    private boolean inputMode = false;

    public InputHandler(Client client) {
        initCommands();
        READER = new BufferedReader(new InputStreamReader(System.in));
        CLIENT = client;
        new LoopThread(this, 50);
    }

    public void initCommands() {
        new Command(new String[]{"/connect", "<host>", "<port>", "<public key>"}, "Connect to server", (client, args) -> client.connect(args[1], Integer.parseInt(args[2]), args[3]));
        new Command(new String[]{"/users"}, "List users", (client, args) -> client.send(new PUserList("")));
        new Command(new String[]{"/close"}, "Close connection or exit", (client, args) -> client.close());
        new Command(new String[]{"/?"}, "List commands", (client, args) -> Command.printHelp());
        new Command(new String[]{"/help", "<command>"}, "Get help for a command", (client, args) -> Command.printHelp(args[1]));
    }

    @Override
    public void run() {
        try {
            handle(READER.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handle(String input) {
        if (inputMode) {
            if (input.isEmpty()) {
                System.out.print("> ");
            } else {
                if (input.startsWith("/")) {
                    String[] command = input.split(" ");
                    Command.handle(CLIENT, command);
                } else {
                    CLIENT.send(input);
                }
                CLIENT.setOutputHandlerFlush(true);
                inputMode = false;
            }
        } else {
            if (input.isEmpty()) {
                CLIENT.setOutputHandlerFlush(false);
                inputMode = true;
                System.out.print("> ");
            } else {
                System.out.println("<Press [ENTER] to enter input mode>");
            }
        }
    }
}
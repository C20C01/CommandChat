package io.github.c20c01.commandchat.client;

import java.util.HashMap;

public record Command(String[] command, String description, CommandRunnable action) {
    private static final HashMap<String, Command> COMMANDS = new HashMap<>();

    public Command {
        COMMANDS.put(command[0], this);
    }

    public int length() {
        return command.length;
    }

    public static void printHelp(String command) {
        command = "/" + command;
        String help = COMMANDS.containsKey(command) ? help(COMMANDS.get(command)) : "Unknown command";
        System.out.println(help);
    }

    public static void printHelp() {
        for (Command command : COMMANDS.values()) {
            System.out.println(help(command));
        }
    }

    public static String help(Command command) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < command.length(); i++) {
            builder.append(command.command[i]).append(" ");
        }
        builder.append("- ").append(command.description);
        return builder.toString();
    }

    public static void handle(Client client, String[] input) {
        if (COMMANDS.containsKey(input[0])) {
            Command command = COMMANDS.get(input[0]);
            if (input.length != command.length()) {
                System.out.println(help(command));
            } else {
                try {
                    command.action.run(client, input);
                } catch (Exception e) {
                    System.out.println("Failed to execute command, reason: " + e.getMessage());
                }
            }
        } else {
            System.out.println("Unknown command");
        }
    }

    public interface CommandRunnable {
        void run(Client client, String[] args);
    }
}

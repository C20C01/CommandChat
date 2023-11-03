package io.github.c20c01.commandchat.client;

import io.github.c20c01.commandchat.pkg.IPackage;
import io.github.c20c01.commandchat.util.Chat;
import io.github.c20c01.commandchat.util.Connection;

import java.net.InetSocketAddress;

public class Client {
    private final OutputHandler OUTPUT_HANDLER;
    private Chat chat;

    public Client() {
        new InputHandler(this);
        OUTPUT_HANDLER = new OutputHandler();
        System.out.println("Client started, input \"?\" for help");
        System.out.println("<Press [Enter] to enter input mode>");
    }

    public void close() {
        if (chat == null) {
            System.out.println("Bye!");
            System.exit(0);
        } else {
            chat.close();
            chat = null;
            System.out.println("Client closed");
        }
    }

    public void send(IPackage pkg) {
        if (chat == null) {
            System.out.println("Not connected to server");
            return;
        }
        chat.send(pkg);
    }

    public void send(String input) {
        if (chat == null) {
            System.out.println("Not connected to server");
            return;
        }
        chat.send(input);
    }

    public void print(String s) {
        OUTPUT_HANDLER.print(s);
    }

    public void setOutputHandlerFlush(boolean flush) {
        if (flush) {
            OUTPUT_HANDLER.startFlush();
        } else {
            OUTPUT_HANDLER.stopFlush();
        }
    }

    public void connect(String ip, int port, String publicKey) {
        try {
            if (chat != null) {
                System.out.println("Already connected to server");
                return;
            }
            Connection connection = new Connection.BuilderOnClient(new InetSocketAddress(ip, port), publicKey).build();
            chat = new ChatOnClient(this, connection, () -> chat = null);
        } catch (Exception e) {
            System.out.println("Failed to connect to server");
        }
    }
}
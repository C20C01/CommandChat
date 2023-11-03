package io.github.c20c01.commandchat.server;

import io.github.c20c01.commandchat.pkg.IPackage;
import io.github.c20c01.commandchat.pkg.PMessage;
import io.github.c20c01.commandchat.pkg.PUserList;
import io.github.c20c01.commandchat.util.Chat;
import io.github.c20c01.commandchat.util.Connection;
import io.github.c20c01.commandchat.util.encryption.RSATool;

import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.security.KeyPair;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class Server {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final ServerSocketChannel SERVER_SOCKET_CHANNEL;
    private final HashMap<InetSocketAddress, Chat> CHATS = new HashMap<>();
    private final KeyPair KEY_PAIR;

    public Server(int port) {
        try {
            SERVER_SOCKET_CHANNEL = ServerSocketChannel.open();
            SERVER_SOCKET_CHANNEL.bind(new InetSocketAddress(port));
            KEY_PAIR = RSATool.generateRSAKey();
            System.out.println("Server started on port: " + port + ", Public key:\n" + getStringPublicKey());
            while (!Thread.currentThread().isInterrupted()) {
                accept();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getStringPublicKey() {
        return RSATool.getPublicKeyString(KEY_PAIR.getPublic());
    }

    public void accept() {
        try {
            Connection connection = new Connection.BuilderOnServer(SERVER_SOCKET_CHANNEL.accept(), KEY_PAIR.getPrivate()).build();
            InetSocketAddress address = connection.getRemoteAddress();
            CHATS.put(address, new ChatOnServer(this, connection, () -> CHATS.remove(address)));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Wrong secret key received");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void print(String s) {
        System.out.println(s);
    }

    public void sendAll(IPackage pkg) {
        CHATS.forEach((k, v) -> v.send(pkg));
    }

    public void sendTo(IPackage pkg, InetSocketAddress address) {
        if (CHATS.containsKey(address)) {
            CHATS.get(address).send(pkg);
        } else {
            System.out.println("Trying to send to unknown address");
        }
    }

    public void handleMessages(String message, InetSocketAddress senderAddress) {
        String dateTime = LocalDateTime.now().format(FORMATTER);
        String newMessage = "[" + dateTime + "] " + senderAddress + ": " + message;
        sendAll(new PMessage(newMessage));
        print(newMessage);
    }

    public void checkAllUsers(InetSocketAddress senderAddress) {
        StringBuilder builder = new StringBuilder();
        CHATS.forEach((k, v) -> builder.append(k).append(", "));
        sendTo(new PUserList(builder.toString()), senderAddress);
        print("User list sent to " + senderAddress);
    }
}

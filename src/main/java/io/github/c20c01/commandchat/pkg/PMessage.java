package io.github.c20c01.commandchat.pkg;

import io.github.c20c01.commandchat.client.Client;
import io.github.c20c01.commandchat.server.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

public record PMessage(String message) implements IPackage {
    public static PMessage decode(DataInputStream stream) throws IOException {
        return new PMessage(stream.readUTF());
    }

    @Override
    public byte id() {
        return ID_MESSAGE;
    }

    @Override
    public void encode(DataOutputStream stream) throws IOException {
        stream.writeUTF(message);
    }

    @Override
    public void handleOnClient(Client client) {
        client.print(message);
    }

    @Override
    public void handleOnServer(Server server, InetSocketAddress senderAddress) {
        server.handleMessages(message, senderAddress);
    }
}

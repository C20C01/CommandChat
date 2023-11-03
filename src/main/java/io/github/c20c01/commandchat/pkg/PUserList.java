package io.github.c20c01.commandchat.pkg;

import io.github.c20c01.commandchat.client.Client;
import io.github.c20c01.commandchat.server.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

public record PUserList(String list) implements IPackage {
    public static PUserList decode(DataInputStream stream) throws IOException {
        return new PUserList(stream.readUTF());
    }

    @Override
    public byte id() {
        return ID_USER_LIST;
    }

    @Override
    public void encode(DataOutputStream stream) throws IOException {
        stream.writeUTF(list);
    }

    @Override
    public void handleOnClient(Client client) {
        client.print("Users: " + list);
    }

    @Override
    public void handleOnServer(Server server, InetSocketAddress senderAddress) {
        server.checkAllUsers(senderAddress);
    }
}

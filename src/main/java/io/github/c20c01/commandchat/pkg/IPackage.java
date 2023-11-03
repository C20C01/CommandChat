package io.github.c20c01.commandchat.pkg;


import io.github.c20c01.commandchat.client.Client;
import io.github.c20c01.commandchat.server.Server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

public interface IPackage {
    byte ID_MESSAGE = 0;
    byte ID_USER_LIST = 1;

    byte id();

    void encode(DataOutputStream stream) throws IOException;

    void handleOnClient(Client client);

    void handleOnServer(Server server, InetSocketAddress senderAddress);
}

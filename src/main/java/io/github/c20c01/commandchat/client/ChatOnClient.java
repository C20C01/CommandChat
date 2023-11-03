package io.github.c20c01.commandchat.client;

import io.github.c20c01.commandchat.pkg.IPackage;
import io.github.c20c01.commandchat.util.Chat;
import io.github.c20c01.commandchat.util.Connection;
import io.github.c20c01.commandchat.util.TaskProcessor;

import java.util.function.Function;

public class ChatOnClient extends Chat {
    private final Client CLIENT;

    public ChatOnClient(Client client, Connection connection, Runnable closeTask) {
        super(closeTask);
        CLIENT = client;
        connected(connection);
    }

    @Override
    protected Function<Integer, TaskProcessor<IPackage>> packageHandler() {
        return (sleepTime) -> new TaskProcessor<>(sleepTime) {
            @Override
            protected void process(IPackage task) {
                task.handleOnClient(CLIENT);
            }
        };
    }
}

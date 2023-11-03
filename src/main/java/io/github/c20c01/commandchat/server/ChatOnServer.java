package io.github.c20c01.commandchat.server;

import io.github.c20c01.commandchat.pkg.IPackage;
import io.github.c20c01.commandchat.util.Chat;
import io.github.c20c01.commandchat.util.Connection;
import io.github.c20c01.commandchat.util.TaskProcessor;

import java.util.function.Function;

public class ChatOnServer extends Chat {
    private final Server SERVER;

    public ChatOnServer(Server server, Connection connection, Runnable closeTask) {
        super(closeTask);
        SERVER = server;
        connected(connection);
    }

    @Override
    protected Function<Integer, TaskProcessor<IPackage>> packageHandler() {
        return (sleepTime) -> new TaskProcessor<>(sleepTime) {
            @Override
            protected void process(IPackage task) {
                task.handleOnServer(SERVER, getSenderAddress());
            }
        };
    }
}

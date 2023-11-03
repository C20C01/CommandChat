package io.github.c20c01.commandchat.util;

import io.github.c20c01.commandchat.pkg.IPackage;
import io.github.c20c01.commandchat.pkg.PMessage;

import java.net.InetSocketAddress;
import java.util.function.Function;

public abstract class Chat {
    private final SendProcessor SEND_PROCESSOR;
    private final ReceiveProcessor RECEIVE_PROCESSOR;
    private final Runnable CLOSE_TASK;
    private Connection connection;

    public Chat(Runnable closeTask) {
        CLOSE_TASK = closeTask;
        SEND_PROCESSOR = new SendProcessor(this, 100);
        RECEIVE_PROCESSOR = new ReceiveProcessor(this, 80, packageHandler());
    }

    protected abstract Function<Integer, TaskProcessor<IPackage>> packageHandler();

    public void connected(Connection connection) {
        this.connection = connection;
        SEND_PROCESSOR.setConnection(connection);
        RECEIVE_PROCESSOR.setConnection(connection);
        System.out.println("Connected with " + connection.getRemoteAddress());
    }

    public void close() {
        RECEIVE_PROCESSOR.close();
        SEND_PROCESSOR.close();
        connection.disconnect();
        CLOSE_TASK.run();
    }

    public InetSocketAddress getSenderAddress() {
        return connection.getRemoteAddress();
    }

    public void send(String message) {
        send(new PMessage(message));
    }

    public void send(IPackage pkg) {
        SEND_PROCESSOR.newTask(pkg);
    }
}

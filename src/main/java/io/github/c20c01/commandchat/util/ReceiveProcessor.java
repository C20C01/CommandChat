package io.github.c20c01.commandchat.util;

import io.github.c20c01.commandchat.pkg.IPackage;

import java.util.function.Function;

public class ReceiveProcessor implements Runnable {
    private final Chat COMMAND_CHAT;
    private final LoopThread THREAD;
    private final TaskProcessor<IPackage> PACKAGE_PROCESSOR;
    private Connection connection;

    public ReceiveProcessor(Chat chat, int sleepTime, Function<Integer, TaskProcessor<IPackage>> packageHandler) {
        COMMAND_CHAT = chat;
        THREAD = new LoopThread(this, sleepTime);
        PACKAGE_PROCESSOR = packageHandler.apply(sleepTime);
    }

    @Override
    public void run() {
        if (connection != null) {
            try {
                IPackage pkg = connection.receive();
                if (pkg != null) {
                    PACKAGE_PROCESSOR.newTask(pkg);
                }
            } catch (Exception e) {
                System.out.println("ReceiveProcessor: " + e.getMessage());
                COMMAND_CHAT.close();
            }
        }
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void close() {
        THREAD.interrupt();
        PACKAGE_PROCESSOR.close();
    }
}

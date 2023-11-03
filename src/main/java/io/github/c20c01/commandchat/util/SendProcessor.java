package io.github.c20c01.commandchat.util;

import io.github.c20c01.commandchat.pkg.Encoder;
import io.github.c20c01.commandchat.pkg.IPackage;

public class SendProcessor extends TaskProcessor<IPackage> {
    private final Chat COMMAND_CHAT;
    private Connection connection;

    public SendProcessor(Chat chat, int sleepTime) {
        super(sleepTime);
        COMMAND_CHAT = chat;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    @Override
    protected void process(IPackage task) {
        if (connection != null) {
            try {
                connection.send(Encoder.encode(task));
            } catch (Exception e) {
                System.out.println("SendProcessor: " + e.getMessage());
                COMMAND_CHAT.close();
            }
        }
    }
}

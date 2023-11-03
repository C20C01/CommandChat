package io.github.c20c01.commandchat.client;

import io.github.c20c01.commandchat.util.LoopThread;

import java.io.PrintWriter;
import java.util.LinkedList;

public class OutputHandler implements Runnable {
    private final PrintWriter WRITER = new PrintWriter(System.out);
    private final LinkedList<String> QUEUE = new LinkedList<>();
    private boolean autoFlush = true;

    public OutputHandler() {
        new LoopThread(this);
    }

    @Override
    public void run() {
        if (!QUEUE.isEmpty()) {
            do {
                handle(QUEUE.poll());
            } while (!QUEUE.isEmpty());
            if (autoFlush) {
                WRITER.flush();
            }
        }
    }

    private void handle(String input) {
        WRITER.println(input);
    }

    public void print(String input) {
        QUEUE.add(input);
    }

    public void startFlush() {
        if (!autoFlush) {
            autoFlush = true;
            WRITER.flush();
        }
    }

    public void stopFlush() {
        if (autoFlush) {
            autoFlush = false;
        }
    }
}

package io.github.c20c01.commandchat.util;

import java.util.LinkedList;

public abstract class TaskProcessor<T> implements Runnable {
    private final LinkedList<T> TASKS = new LinkedList<>();
    private final LoopThread THREAD;

    public TaskProcessor(int sleepTime) {
        THREAD = new LoopThread(this, sleepTime);
    }

    protected abstract void process(T task);

    @Override
    public void run() {
        if (!TASKS.isEmpty()) {
            process(TASKS.poll());
        }
    }

    public void close() {
        THREAD.interrupt();
    }

    public void newTask(T task) {
        TASKS.add(task);
    }
}

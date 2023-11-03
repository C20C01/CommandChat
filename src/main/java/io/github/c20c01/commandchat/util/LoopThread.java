package io.github.c20c01.commandchat.util;

public class LoopThread extends Thread {
    private final int SLEEP_TIME;

    public LoopThread(Runnable runnable) {
        this(runnable, 100);
    }

    public LoopThread(Runnable runnable, int sleepTime) {
        super(runnable);
        SLEEP_TIME = sleepTime;
        this.start();
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            super.run();
            takeNap();
        }
    }

    private void takeNap() {
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            this.interrupt();
        }
    }
}

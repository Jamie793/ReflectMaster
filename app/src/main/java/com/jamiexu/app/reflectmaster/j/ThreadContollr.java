package com.jamiexu.app.reflectmaster.j;

public class ThreadContollr extends Thread {
    private Thread thread;
    private boolean isPause;

    public ThreadContollr(Thread thread) {
        this.thread = thread;
    }

    public synchronized void pauseT() {
        this.isPause = true;
    }

    public synchronized void continueT() {
        this.isPause = false;
        this.thread.notify();
    }

    public void run() {
        super.run();
        while (true) {
            if (isPause) {
                try {
                    this.thread.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

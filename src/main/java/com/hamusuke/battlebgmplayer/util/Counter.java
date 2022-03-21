package com.hamusuke.battlebgmplayer.util;

public class Counter {
    private final int bound;
    private int counter;

    public Counter(int bound) {
        this.bound = bound;
    }

    public void reset() {
        this.counter = 0;
    }

    public boolean count() {
        if (this.counter >= Integer.MAX_VALUE) {
            this.reset();
        }

        return this.counter++ >= this.bound;
    }
}

package com.richal.learn;

public interface RejectHandle {

    void reject(Runnable command, MyThreadPool threadPool);
}

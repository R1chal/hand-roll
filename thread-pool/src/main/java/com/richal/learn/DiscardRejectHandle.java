package com.richal.learn;

public class DiscardRejectHandle implements RejectHandle{
    @Override
    public void reject(Runnable rejectCommand, MyThreadPool threadPool) {
        threadPool.workQueue.poll();
        threadPool.execute(rejectCommand);
    }
}

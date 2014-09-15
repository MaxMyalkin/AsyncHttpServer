package server;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ThreadPool {
    private final ConcurrentLinkedQueue<Worker> workers = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<AsynchronousSocketChannel> channels = new ConcurrentLinkedQueue<>();
    public ThreadPool() throws Throwable {
        for(int i = 0; i < Server.THREADS; i++){
            Worker w = new Worker(this);
            workers.add(w);
            new Thread(w).start();
        }
    }


    public synchronized void addWorker(Worker worker) {
        AsynchronousSocketChannel channel = channels.poll();
        if(channel != null) {
            worker.setChannel(channel);
        }
        else
            workers.add(worker);
    }

    public synchronized void addSocketChannel(AsynchronousSocketChannel channel) {
        Worker worker = workers.poll();
        if(worker != null) {
            worker.setChannel(channel);
        }
        else
            channels.add(channel);
    }
}

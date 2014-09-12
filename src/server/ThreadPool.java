package server;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.Random;

public class ThreadPool {
    private final ArrayList<Worker> workers = new ArrayList<>();
    private Random random = new Random();
    public ThreadPool() throws Throwable {
        for(int i = 0; i < Server.THREADS; i++){
            Worker w = new Worker();
            workers.add(w);
            new Thread(w).start();
        }
    }

    public synchronized void addSocketChannel(AsynchronousSocketChannel channel) {
        workers.get(random.nextInt(Server.THREADS)).addChannel(channel);
    }
}

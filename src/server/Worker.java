package server;

import handlers.SocketReadHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public class Worker implements Runnable {
    public static final int TIMEOUT = 100;

    private ThreadPool pool;
    private AsynchronousSocketChannel channel;

    public Worker(ThreadPool pool) {
        this.pool = pool;
    }

    public synchronized void run() {
        while (true) {
            if(channel == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                handleRequest();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                channel = null;
                pool.addWorker(this);
            }
        }
    }

    private void handleRequest() throws IOException {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 10);
            channel.read(buffer, TIMEOUT, new SocketReadHandler<Integer, Integer>(buffer, channel));
        }
        catch (Exception ignored) {
        }
    }

    public synchronized void setChannel(AsynchronousSocketChannel channel) {
        if(channel != null) {
            this.channel = channel;
            notify();
        }
    }
}
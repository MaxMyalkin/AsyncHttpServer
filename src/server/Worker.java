package server;

import handlers.SocketReadHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Worker implements Runnable {
    public static final int TIMEOUT = 100;

    private AsynchronousSocketChannel channel;
    private ConcurrentLinkedQueue<AsynchronousSocketChannel> channels = new ConcurrentLinkedQueue<>();

    public synchronized void run() {
        while (true) {
            channel = channels.poll();
            if(channel == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    //continue;
                }
            }
            try {
                handleRequest();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                channel = null;
            }
        }
    }

    private void handleRequest() throws IOException {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 10);
            channel.read(buffer, TIMEOUT, new SocketReadHandler<Integer, Integer>(buffer, channel));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void addChannel(AsynchronousSocketChannel channel) {
        if(channel != null) {
            channels.add(channel);
            this.channel = channel;
            notify();
        }
    }
}
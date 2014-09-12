package handlers;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;


public class SocketWriteHandler<K, Boolean> implements CompletionHandler<K, Boolean> {

    private AsynchronousSocketChannel socketChannel;

    public SocketWriteHandler(AsynchronousSocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void completed(K result, Boolean attachment) {
        try {
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failed(Throwable exc, Boolean attachment) {
        try {
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

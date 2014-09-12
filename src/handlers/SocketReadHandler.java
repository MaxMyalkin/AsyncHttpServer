package handlers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class SocketReadHandler<K, V> implements CompletionHandler<K, V> {

    private ByteBuffer buffer;
    private AsynchronousSocketChannel socketChannel;

    public SocketReadHandler(ByteBuffer buffer, AsynchronousSocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        this.buffer = buffer;
    }

    @Override
    public void completed(K result, V attachment) {
        try {
            String request = new String(buffer.array());
            Handler handler = new Handler(request, socketChannel);
            handler.parseRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void failed(Throwable exc, V attachment) {

    }
}

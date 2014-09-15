package handlers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;


public class SocketWriteHandler implements CompletionHandler<Integer, Void> {

    private AsynchronousSocketChannel socketChannel;
    private ByteBuffer buffer;

    public SocketWriteHandler(AsynchronousSocketChannel socketChannel, ByteBuffer buffer) {
        this.socketChannel = socketChannel;
        this.buffer = buffer;
    }

    @Override
    public void completed(Integer result, Void attachment) {
        if(buffer.hasRemaining()) {
            socketChannel.write(buffer, null, this);
        } else {
            try {
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void failed(Throwable exc, Void attachment) {
        try {
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

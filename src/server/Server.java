package server;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class Server {
    public static final int PORT = 9000;
    public static final int THREADS = 8;
    public static final int BACKLOG = 100;

    public static void main(String[] args) throws Throwable {

        final AsynchronousServerSocketChannel server =
                AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(PORT), BACKLOG);

        final ThreadPool pool = new ThreadPool();

        server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
            @Override
            public void completed(AsynchronousSocketChannel channel, Void a) {
                server.accept(null, this);
                pool.addSocketChannel(channel);
            }

            @Override
            public void failed(Throwable exc, Void a) {

            }

        });

        while (true) {
        }
    }
}

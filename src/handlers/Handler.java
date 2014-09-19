package handlers;


import response.Parameters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.file.StandardOpenOption;

public class Handler {
    public static final String DOCUMENT_ROOT = "/home/maxim/Projects/HighLoad/WebServer/static";

    private AsynchronousSocketChannel socketChannel;
    private Parameters parameters;

    public Handler(String request, AsynchronousSocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        parameters = new Parameters(request);
    }

    public void parseRequest() throws IOException {
        switch (parameters.getMethod()) {
            case "GET":
            case "HEAD":
                parameters.setCode(200);
                readFile();
                break;
            default:
                parameters.setCode(405);
                writeToSocket();
        }
    }


    private void readFile() {
        if(parameters.getPath() != null) {
            boolean isDirectory = false;
            String dir = DOCUMENT_ROOT + parameters.getPath();
            File file = new File(dir);
            try {
                if (!file.getCanonicalPath().contains(DOCUMENT_ROOT)) {
                    parameters.setCode(403);
                    writeToSocket();
                } else {
                    if (file.isDirectory()) {
                        dir += "index.html";
                        file = new File(dir);
                        isDirectory = true;
                        parameters.setSuffix(".html");
                    }
                    if (!file.exists()) {
                        throw new FileNotFoundException("File not found");
                    } else
                        parameters.setLength(file.length());

                    switch (parameters.getMethod()) {
                        case "GET":
                            AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(file.toPath(), StandardOpenOption.READ);
                            ByteBuffer buffer = ByteBuffer.allocate((int) file.length());
                            fileChannel.read(buffer, 0, null, new FileReadHandler<Integer, Void>(this, buffer, fileChannel));
                            break;
                        default:
                            writeToSocket();
                    }
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                if (isDirectory)
                    parameters.setCode(403);
                else
                    parameters.setCode(404);
                writeToSocket();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeToSocket() {
        String responseStr = parameters.getHeader();
        ByteBuffer buffer = ByteBuffer.wrap(responseStr.getBytes());
        buffer.position(0);
        try {
            socketChannel.setOption(StandardSocketOptions.SO_SNDBUF, buffer.capacity());
        } catch (IOException e) {
            e.printStackTrace();
        }
        SocketWriteHandler handler = new SocketWriteHandler(socketChannel, buffer);
        socketChannel.write(buffer, null, handler);

    }

    public void writeToSocket(ByteBuffer buffer) {
        String header = parameters.getHeader();
        int length = header.length() + buffer.capacity();
        try {
            buffer.position(0);
            ByteBuffer newBuffer = ByteBuffer.allocate(length)
                    .put(header.getBytes())
                    .put(buffer);
            socketChannel.setOption(StandardSocketOptions.SO_SNDBUF, newBuffer.capacity());
            SocketWriteHandler handler = new SocketWriteHandler(socketChannel, newBuffer);
            newBuffer.position(0);
            socketChannel.write(newBuffer, null, handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

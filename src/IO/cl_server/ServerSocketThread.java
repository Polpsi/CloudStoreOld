package IO.cl_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ServerSocketThread extends Thread {

    private final int port;

    public ServerSocketThread(String name, int port) {
        super(name);
        this.port = port;
        start();
    }

    @Override
    public void run() {
        try (ServerSocket server = new ServerSocket(port)) {
            while (!isInterrupted()) {
                Socket socket;
                try {
                    socket = server.accept();
                } catch (SocketTimeoutException e) {
                    continue;
                }
                session_start(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void session_start(Socket socket) {
        new SocketThread("socket", socket);
    }
}


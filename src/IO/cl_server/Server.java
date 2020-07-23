package IO.cl_server;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    ServerSocketThread server;

    public Server() throws IOException {
        server = new ServerSocketThread("Server", 8989);
    }

    public static void main(String[] args) throws IOException {
        new Server();
    }

}

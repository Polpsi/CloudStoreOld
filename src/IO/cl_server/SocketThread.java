package IO.cl_server;

import java.io.*;
import java.net.Socket;

public class SocketThread extends Thread {

    Socket socket;
    String[] request;

    public SocketThread(String name, Socket socket) {
        super(name);
        this.socket = socket;
        start();
    }

    @Override
    public void run() {
        DataInputStream is = null;
        try {
            is = new DataInputStream(socket.getInputStream());
            request = is.readUTF().split("!!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String userID = request[0];
        File userDir = new File("./serverdir/" + userID);
        if (!userDir.exists()) {
            userDir.mkdir();
        }
        try {
            switch (request[1]) {
                case "getList":
                    getList(userDir, socket);
                    break;
                case "getFile": //команды от пользователя, поэтому инвертируем
                    sendFileToUser(request[2], socket, userDir.getPath());
                    break;
                case "sendFile":
                    getFileFromUser(request[2], socket, userDir.getPath());
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void getFileFromUser(String fileName, Socket socket, String userDir) throws IOException {
        File file = new File(userDir + "\\" + fileName);
        try (FileOutputStream os = new FileOutputStream(file)) {
            DataInputStream is = new DataInputStream(socket.getInputStream());
            byte[] buffer = new byte[8192];
            while (true) {
                int r = is.read(buffer);
                if (r == -1) break;
                os.write(buffer, 0, r);
            }
            System.out.println("File " + file.getName() + " download");
        }
    }

    private void sendFileToUser(String fileName, Socket socket, String userDir) throws IOException {
        File file = new File(userDir + "\\" + fileName);
        try (DataOutputStream os = new DataOutputStream(socket.getOutputStream())) {
            FileInputStream is = new FileInputStream(file);
            System.out.println(file.getAbsolutePath() + " start send");
            byte[] buffer = new byte[8192];
            while (is.available() > 0) {
                int r = is.read(buffer);
                os.write(buffer, 0, r);
            }
            System.out.println("File " + file.getName() + " sended.");
        }
    }

    private void getList(File userDir, Socket socket) throws IOException {
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());
        String[] list = userDir.list();
        String answer = "";
        for (String name : list) {
            answer += name + "\n";
        }
        if (answer == "") {
            answer = "Directory empty \n";
        }
        os.writeUTF(answer);
    }
}
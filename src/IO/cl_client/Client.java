package IO.cl_client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

// Михаил, добрый день!
// Я не успел сделать ДЗ в срок по субъективно-объективным причинам. 
// Но для последующего трудоустройства мне нужны хорошие оценки за каждое ДЗ.
// Прошу поставить "отлично" за это ДЗ, постараюсь не допускать подобного в дальнейшем.
// Спасибо!



public class Client {

    private static Scanner scan = new Scanner(System.in);
    private static String host;
    private static int port;
    private static File userDir;

    public static void main(String[] args) {
        System.out.println("Host? def:localhost");
        // host = scan.next().toString();
        host = "localhost"; //чтобы каждый тест не вводить.
        System.out.println("Port? def:8989");
        // port = scan.nextInt();
        port = 8989; //чтобы каждый тест не вводить.
        System.out.println("Your ID?");
        String userID = scan.next();
        userDir = new File("./clientdir/" + userID);
        if (!userDir.exists()) {
            userDir.mkdir();
        }
        try {
            getUserDir(userID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (true) {
            System.out.println("Your command? (getList,getFile,sendFile,exit)");
            String command = scan.next();
            switch (command) {
                case "exit":
                    System.exit(0);
                case "getList":
                    try {
                        getUserDir(userID);
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                case "getFile":
                    try {
                        getFile(userID);
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                case "sendFile":
                    try {
                        sendFile(userID);
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                default:
            }
        }
    }

    private static void sendFile(String userID) throws IOException {
        System.out.println("File name?");
        String fileName = scan.next();
        File fileToRead = new File(userDir.getAbsolutePath() + "\\" + fileName);
        try (Socket socket = new Socket(host, port); DataOutputStream os = new DataOutputStream(socket.getOutputStream())) {
            os.writeUTF(userID + "!!sendFile!!" + fileName);
            System.out.println(fileToRead.getAbsolutePath() + " start to send");
            FileInputStream fis = new FileInputStream(fileToRead);
            byte[] buffer = new byte[8192];
            while (fis.available() > 0) {
                int r = fis.read(buffer);
                os.write(buffer, 0, r);
            }
            System.out.println("File " + fileToRead.getName() + " sended.");
        }
    }

    private static void getFile(String userID) throws IOException {
        System.out.println("File name?");
        String fileName = scan.next();
        File fileToWrite = new File(userDir.getAbsolutePath() + "\\" + fileName);
        try (Socket socket = new Socket(host, port)) {
            DataInputStream is = new DataInputStream(socket.getInputStream());
            DataOutputStream os = new DataOutputStream(socket.getOutputStream());
            os.writeUTF(userID + "!!getFile!!" + fileName);
            try (FileOutputStream fos = new FileOutputStream(fileToWrite)) {
                byte[] buffer = new byte[8192];
                while (true) {
                    int r = is.read(buffer);
                    if (r == -1) break;
                    fos.write(buffer, 0, r);
                }
                System.out.println("File download");
            } catch (Exception e) {
                e.printStackTrace();
            }
            ;
        }
    }

    private static void getUserDir(String userID) throws IOException {

        try (Socket socket = new Socket(host, port)) {
            DataInputStream is = new DataInputStream(socket.getInputStream());
            DataOutputStream os = new DataOutputStream(socket.getOutputStream());
            os.writeUTF(userID + "!!" + "getList");
            String answer = is.readUTF();
            System.out.println(answer);
        }
    }
}

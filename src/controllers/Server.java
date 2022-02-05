package controllers;

import javafx.application.Platform;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    static Socket socket;
    static Thread x;
    private static ServerSocket serverSocket;


    public Server() {
        try {
            serverSocket = new ServerSocket(5001);
            startingServer();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // open server
    public static void startingServer() {
        if (serverSocket == null) {
            System.out.println("null found ");
        } else {


            x = new Thread(() -> {
                while (!serverSocket.isClosed()) {
                    try {
                        socket = serverSocket.accept();
                        new ServerHandler(socket);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            x.start();

        }
    }

    // closing server
    public static void close() {

        if (serverSocket != null) {
            try {
                x.stop();
                serverSocket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    public static void main(String[] args) {
//        new Server();
//    }
}

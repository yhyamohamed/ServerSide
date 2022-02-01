package serverTest;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket serverSocket;
    private ServerHandler SHandler;


    public Server()  {
        try {
            serverSocket = new ServerSocket(6654);

        } catch (IOException e) {
            e.printStackTrace();
        }
        startingServer();
    }

    private void startingServer() {
        while (!serverSocket.isClosed())
        {
            Socket socket ;
            try {
                socket = serverSocket.accept();
                new ServerHandler(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    public void close()
    {
        if(serverSocket !=null){
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("server closing");
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}

package controllers;

import com.google.gson.JsonObject;

import java.io.*;
import java.net.Socket;


public class Client {
    Socket socket;
    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;
    JsonObject jsonObject;

    public void listenForServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
    }

    public Client() {
        try {
            socket = new Socket("127.0.0.1", 5001);
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            jsonObject = new JsonObject();
            jsonObject.addProperty("type", "finish_game");
//            jsonObject.addProperty("username", "ismail");
//            jsonObject.addProperty("password", "root");
            jsonObject.addProperty("game_id", "5");
            jsonObject.addProperty("winner", "abdallah");
//            jsonObject.addProperty("player_x_id", "2");
//            jsonObject.addProperty("player_o_id", "1");
//            jsonObject.addProperty("step_number", "2");
//            jsonObject.addProperty("step", "[9, 2, 4, 5, 8, 2, 5, 4, 3]");
            dataOutputStream.writeUTF(jsonObject.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) socket.close();
                if (dataOutputStream != null) dataOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Client();
    }

}

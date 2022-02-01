package serverTest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class ServerHandler extends  Thread  {

        private  boolean running;

        static ArrayList<ServerHandler> clients = new ArrayList<>();
        private DataInputStream dataInputStream;
        private DataOutputStream dataOutputStream;



        public ServerHandler(Socket c)
        {
            try {
                dataInputStream = new DataInputStream(c.getInputStream());
                dataOutputStream = new DataOutputStream(c.getOutputStream());
                clients.add(this);
              start();
            } catch (IOException e) {
               close( dataInputStream, dataOutputStream);
            }

        }

    public void broadcast(JsonObject msg) {
        for(ServerHandler client : clients){
            try {
                client.dataOutputStream.writeUTF(msg.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            //client.printStream.flush();
        }
    }

    public void run()
        {
            running=true;
            while (running) {

                try {
                    String lineSent = dataInputStream.readUTF();
                    if(lineSent == null)throw new IOException();
                    JsonObject object = JsonParser.parseString(lineSent).getAsJsonObject();
                    String type = object.get("type").getAsString();
                    if(object == null|| type.equals("close")){
                        leaveNetwork(this);
                        throw new IOException();
                    }
                        broadcast(object);
                } catch (EOFException | SocketException e) {
                    running= false;
                    e.printStackTrace();
                } catch (IOException e) {

                    running= false;
                    close(dataInputStream, dataOutputStream);
                    break;
                }
            }
        }




    public void leaveNetwork(ServerHandler serverHandler){
        clients.remove(serverHandler);
        System.out.println(" left chat");
    }


        public void close(DataInputStream bufferedReader, DataOutputStream bufferedWriter)
        {

            running= false;
            try {
                if(bufferedReader != null)
                    bufferedReader.close();
                if(bufferedWriter!=null)
                    bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


}

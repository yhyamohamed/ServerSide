package serverTest;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends JFrame {

    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    private Socket socket;
    
    public Client()
    {
        try {
             socket = new Socket(InetAddress.getLocalHost(),6654);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            close(socket, dataInputStream, dataOutputStream);
        }
        this.setLayout(new FlowLayout());

        JTextArea ta = new JTextArea(20, 50);
        JScrollPane scroll= new JScrollPane(ta);
        JTextField  tf= new JTextField(40);
        JButton okButton= new JButton("Send");

        okButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                JsonObject obj = new JsonObject();
                obj.addProperty("msg",tf.getText());
                obj.addProperty("type","login");
                try {
                    dataOutputStream.writeUTF(obj.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                tf.setText(" ");
            }
        });


        add(scroll);
        add(tf);
        add(okButton);
        listenToStream(ta);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to close this window?", "Close Window?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){

//                        printStream.println("close");
                    JsonObject obj = new JsonObject();

                    obj.addProperty("type","close");

                    try {
                        dataOutputStream.writeUTF(obj.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    close(socket,dataInputStream, dataOutputStream);
                    System.exit(0);
                }
            }
        });
    }

    private void close(Socket socket, DataInputStream dataInputStream, DataOutputStream printStream) {
        try {
            if(dataInputStream != null)
                dataInputStream.close();
            if(printStream!=null)
                printStream.close();
            if(socket!= null)
                socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/*
    public void senMsg(){
        try {

            printStream.write(clientUserName);
            printStream.newLine();
            printStream.flush();
            Scanner scanner = new Scanner(System.in);

            while(socket.isConnected()){
                String msg = scanner.nextLine();
                printStream.write(clientUserName +": "+ msg);
                printStream.newLine();
                printStream.flush();
            }
        } catch (IOException e) {
            close(socket, dataInputStream, printStream);
        }

    }
*/
    public void sendAnnounce(JsonObject ob) {
        try {
            dataOutputStream.writeUTF(ob.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void listenToStream(JTextArea ta){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (socket.isConnected()){
                    String dataOnStream;

                    try {
                        dataOnStream = dataInputStream.readUTF();
                        if(dataOnStream == null) throw new IOException();

                        JsonObject received = JsonParser.parseString(dataOnStream).getAsJsonObject();
                        String type = received.get("type").getAsString();

                        if(type.equals("close")) throw new IOException();

                        String ms = received.get("msg").toString();
                        ta.append(ms+"\n");
                        System.out.println(dataOnStream);
                    } catch (IOException e) {
                        close(socket, dataInputStream, dataOutputStream);
                        break;
                    }

                }

            }
        }).start();
    }

    public static void main(String[] args) {


        Client client =new Client();

        client.setSize(600, 400);
        client.setResizable(false);
        client.setVisible(true);


    }
}

package controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import Models.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ServerHandler extends Thread {

    private boolean running;

    static ArrayList<ServerHandler> clients = new ArrayList<>();
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private Socket socket;
    static HashMap<Integer,ServerHandler> players = new HashMap<>();
    private int currentID;

    public ServerHandler(Socket c)
    {
        try {
            dataInputStream = new DataInputStream(c.getInputStream());
            dataOutputStream = new DataOutputStream(c.getOutputStream());
            clients.add(this);
            socket=c;
            start();
        } catch (IOException e) {
            close(dataInputStream, dataOutputStream);
        }

    }

    public void run()
    {
        running = true;

        while (running) {

            try {
                String lineSent = dataInputStream.readUTF();
                if(lineSent == null)throw new IOException();
                JsonObject requestObject = JsonParser.parseString(lineSent).getAsJsonObject();
                String type = requestObject.get("type").getAsString();
                JsonObject responseObject = new JsonObject();
                Game game;
                switch (type) {
                    case "login":

                        Player player = login(requestObject);
                        if(player == null){
                            responseObject.addProperty("type","loginresponse");
                            responseObject.addProperty("successful", "false");
                            dataOutputStream.writeUTF(responseObject.toString());
                        } else {
                            System.out.println(player.getId());
                            responseObject.addProperty("type","loginresponse");
                            responseObject.addProperty("successful", "true");
                            responseObject.addProperty("id", player.getId());
                            responseObject.addProperty("username", player.getUsername());
                            responseObject.addProperty("score", player.getScore());
                            responseObject.addProperty("wins", player.getWins());
                            responseObject.addProperty("losses", player.getLosses());
                            players.put(player.getId(),this);
                            this.currentID = player.getId();
                            dataOutputStream.writeUTF(responseObject.toString());
                        }
                        break;

                    case "logout":

                        logout(requestObject);
                        break;

                    case "signup":

                        if(signup(requestObject)) {
                            responseObject.addProperty("successful", "true");
                            dataOutputStream.writeUTF(responseObject.toString());
                        } else {
                            responseObject.addProperty("successful", "false");
                            dataOutputStream.writeUTF(responseObject.toString());
                        }
                        break;

                    case "create_game":

                        game = createGame();
                        System.out.println(game.getId());
                        responseObject.addProperty("game_id", game.getId());
                        //Show gameboard with the response object with the game id
                        break;

                    case "move":

                        GameRecord gameRecord = move(requestObject);
                        if(gameRecord == null) {
                            responseObject.addProperty("successful", "false");
                            //send responseObject to client
                        } else {
                            responseObject.addProperty("successful", "true");
                            responseObject.addProperty("game_id", gameRecord.getGameID());
                            responseObject.addProperty("player_x_id", gameRecord.getPlayerXID());
                            responseObject.addProperty("player_o_id", gameRecord.getPlayerOID());
                            responseObject.addProperty("step_number", gameRecord.getStepNumber());
                            responseObject.addProperty("step", Arrays.toString(gameRecord.getStep()));
                            //send responseObject to client
                        }
                        break;
                    case "play" :
                            int opponentID=Integer.parseInt(requestObject.get("opponet").getAsString());
                        System.out.println("play"+opponentID);
                            String position=requestObject.get("position").getAsString();
                            String sign=requestObject.get("sign").getAsString();
                            ServerHandler opponetSocket=players.get(opponentID);
                            responseObject.addProperty("type","oponnetmove");
                            responseObject.addProperty("position",position);
                            responseObject.addProperty("opponentsing",sign);
                        System.out.println("player position"+position);

                            opponetSocket.dataOutputStream.writeUTF(responseObject.toString());;

                        break;
                    case "getOpponentId" :
                        int id=Integer.parseInt(requestObject.get("playerid").getAsString());
                        System.out.println("getopponentidserver");
                        if(id==1)
                        {
                            responseObject.addProperty("opponentid",2);
                            responseObject.addProperty("turn",true);
                            ServerHandler opponethandler=players.get(id);
                                opponethandler.dataOutputStream.writeUTF(responseObject.toString());
                            System.out.println(id);

                        } else {
                            responseObject.addProperty("opponentid",1);
                            responseObject.addProperty("turn",false);
                            ServerHandler opponethandler=players.get(id);
                            opponethandler.dataOutputStream.writeUTF(responseObject.toString());
                            System.out.println(id);
                        }
                        break;

                    case "finish_game":

                        finishGame(requestObject);
                        break;

                    case "client_close":
                        clients.remove(this);
                        players.remove(this.currentID);
                        System.out.println("Player with id " + this.currentID + " closed the client.");
                        break;

                }
                if(requestObject == null|| type.equals("close")){
                    leaveNetwork(this);
                    throw new IOException();
                }

            } catch (EOFException | SocketException e) {
                running = false;
                close(dataInputStream, dataOutputStream);
            } catch (IOException e) {

                running = false;
                close(dataInputStream, dataOutputStream);
                break;
            }
        }
    }

    public Player login(JsonObject msg) {
        String username = msg.get("username").getAsString();
        String password = msg.get("password").getAsString();
        Player player = new Player();
        return player.login(username, password);
    }

    public void logout(JsonObject msg) {
        String username = msg.get("username").getAsString();
        Player player = new Player();
        player.logout(username);
    }

    public boolean signup(JsonObject msg) {
        String username = msg.get("username").getAsString();
        String password = msg.get("password").getAsString();
        Player player = new Player();
        return player.create(username, password);
    }

    public Game createGame() {
        Game game = new Game();
        return game.create();
    }

    public GameRecord move(JsonObject msg) {
        int gameID = msg.get("game_id").getAsInt();
        int playerXID = msg.get("player_x_id").getAsInt();
        int playerOID = msg.get("player_o_id").getAsInt();
        int stepNumber = msg.get("step_number").getAsInt();
        String[] stepString =  msg.get("step").getAsString().substring(1, msg.get("step").getAsString().length() - 1).split(", ");
        int[] step = Arrays.stream(stepString).mapToInt(Integer::parseInt).toArray();
        GameRecord gameRecord = new GameRecord();
        return gameRecord.create(gameID, playerXID, playerOID, stepNumber, step);
    }

    public void finishGame(JsonObject msg) {
        int gameID = msg.get("game_id").getAsInt();
        String winnerUsername = msg.get("winner").getAsString();
        Game game = new Game();
        game.finishGame(gameID, winnerUsername);
    }


    public void leaveNetwork(ServerHandler serverHandler){
        clients.remove(serverHandler);
        System.out.println("left chat");
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
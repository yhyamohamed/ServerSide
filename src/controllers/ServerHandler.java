package controllers;

import com.google.gson.JsonArray;
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
                ServerHandler opponentSocket;
                int opponentID;
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
                        clients.remove(this);
                        players.remove(this.currentID);
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

//                    case "move":
//
//                        GameRecord gameRecord = move(requestObject);
//                        if(gameRecord == null) {
//                            responseObject.addProperty("successful", "false");
//                            //send responseObject to client
//                        } else {
//                            responseObject.addProperty("successful", "true");
//                            responseObject.addProperty("game_id", gameRecord.getGameID());
//                            responseObject.addProperty("player_x_id", gameRecord.getPlayerXID());
//                            responseObject.addProperty("player_o_id", gameRecord.getPlayerOID());
//                            responseObject.addProperty("step_number", gameRecord.getStepNumber());
//                            responseObject.addProperty("step", Arrays.toString(gameRecord.getStep()));
//                            //send responseObject to client
//                        }
//                        break;
                    case "play" :
                        opponentID=Integer.parseInt(requestObject.get("opponet").getAsString());
                        System.out.println("play"+opponentID);
                            String position=requestObject.get("position").getAsString();
                            String sign=requestObject.get("sign").getAsString();
                            opponentSocket=players.get(opponentID);
                            responseObject.addProperty("type","oponnetmove");
                            responseObject.addProperty("position",position);
                            responseObject.addProperty("opponentsing",sign);

                            requestObject.addProperty("player_id", this.currentID);
                            GameRecord gameRecord = move(requestObject);

                            opponentSocket.dataOutputStream.writeUTF(responseObject.toString());;

                        break;
                    case "getOpponentId" :
                        //int id=Integer.parseInt(requestObject.get("playerid").getAsString());
                        int id = requestObject.get("playerid").getAsInt();
                        System.out.println("getopponentidserver");
                        if(id==1)
                        {
                            responseObject.addProperty("opponentid",2);
                            responseObject.addProperty("turn",true);
                            opponentSocket=players.get(id);
                            opponentSocket.dataOutputStream.writeUTF(responseObject.toString());
                            System.out.println(id);

                        } else {
                            responseObject.addProperty("opponentid",1);
                            responseObject.addProperty("turn",false);
                            opponentSocket=players.get(id);
                            opponentSocket.dataOutputStream.writeUTF(responseObject.toString());
                            System.out.println(id);
                        }
                        break;
                    case "sendInvitation":
                        int senderId=Integer.parseInt(requestObject.get("senderplayerid").getAsString());
                        int receiverId=Integer.parseInt(requestObject.get("sendtoid").getAsString());
                        game = createGame();
                        System.out.println(game.getId());
                        responseObject.addProperty("game_id", game.getId());
                        responseObject.addProperty("type","invitationreceived");
                        responseObject.addProperty("sender",senderId);
                        ServerHandler receiverhandler=players.get(receiverId);
                        System.out.println(senderId+"sended to "+receiverId);
                        System.out.println(receiverhandler);
                        System.out.println(players);
                        receiverhandler.dataOutputStream.writeUTF(responseObject.toString());
                        System.out.println(receiverId);
                        break;
                    case "acceptinvetation":
                        int accepterId=Integer.parseInt(requestObject.get("accepter").getAsString());
                        int acceptedId=Integer.parseInt(requestObject.get("accepted").getAsString());
                        int acceptedGameID=requestObject.get("game_id").getAsInt();
                        responseObject.addProperty("type","yourinvetationaccepted");
                        responseObject.addProperty("game_id",acceptedGameID);
                        responseObject.addProperty("whoaccepted",accepterId);
                        ServerHandler acceptedhandler=players.get(acceptedId);
                        System.out.println(players);
                        acceptedhandler.dataOutputStream.writeUTF(responseObject.toString());

                        break;

                    case "finish_game":

                        finishGame(requestObject);
                        break;

                    case "client_close":
                        clients.remove(this);
                        players.remove(this.currentID);
                        System.out.println("Player with id " + this.currentID + " closed the client.");
                        break;

                    case "client_close_while_playing":
                        clients.remove(this);
                        players.remove(this.currentID);
                        opponentID = requestObject.get("opponentId").getAsInt();
                        opponentSocket = players.get(opponentID);
                        responseObject.addProperty("type", "opponent_disconnect");
                        opponentSocket.dataOutputStream.writeUTF(responseObject.toString());
                        System.out.println("Player with id " + this.currentID + " closed the client while playing.");
                        break;
                    case "getonlineplayers" :
                        Player player1=new Player();
                        JsonArray onlineplayersjsonarr=new JsonArray();
                        ArrayList<Player> onlinePlayers=player1.findOnlinePlayers();
                        for(Player onplayer:onlinePlayers)
                        {
                            JsonObject playerJson=new JsonObject();
                            playerJson.addProperty("username",onplayer.getUsername());
                            playerJson.addProperty("id",onplayer.getId());
                            playerJson.addProperty("score",onplayer.getScore());
                            onlineplayersjsonarr.add(playerJson);
                        }
                        System.out.println(onlineplayersjsonarr);
                        responseObject.add("onlineplayers",onlineplayersjsonarr);
                        responseObject.addProperty("type","onlineplayers");
                        dataOutputStream.writeUTF(responseObject.toString());

                        break;
                    case "getofflineplayers" :
                        Player player2=new Player();
                        JsonArray offlineplayersjsonarr=new JsonArray();
                        ArrayList<Player> offlinePlayers=player2.findOfflinePlayers();
                        for(Player offplayer:offlinePlayers)
                        {
                            JsonObject playerJson=new JsonObject();
                            playerJson.addProperty("username",offplayer.getUsername());
                            playerJson.addProperty("id",offplayer.getId());
                            playerJson.addProperty("score",offplayer.getScore());
                            offlineplayersjsonarr.add(playerJson);
                        }
                        System.out.println(offlineplayersjsonarr);
                        responseObject.add("offlineplayers",offlineplayersjsonarr);
                        responseObject.addProperty("type","offlineplayers");
                        dataOutputStream.writeUTF(responseObject.toString());

                        break;


                    case "request_record":
                        int gameID = requestObject.get("game_id").getAsInt();
                        String[] moves = getMoves(gameID);
                        responseObject.addProperty("type","game_record");
                        requestObject.addProperty("moves", Arrays.toString(moves));
                        dataOutputStream.writeUTF(responseObject.toString());
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
        int playerID = msg.get("player_id").getAsInt();
        int position = msg.get("position").getAsInt();
        int move = msg.get("sign").getAsInt();
        GameRecord gameRecord = new GameRecord();
        return gameRecord.create(gameID, playerID, move, position);
    }

    public void finishGame(JsonObject msg) {
        int gameID = msg.get("game_id").getAsInt();
        String winnerUsername = msg.get("winner").getAsString();
        Game game = new Game();
        game.finishGame(gameID, winnerUsername);
    }

    public String[] getMoves(int gameID) {
        GameRecord gameRecord = new GameRecord();
        ArrayList<GameRecord> movesAL = gameRecord.findByGameID(gameID);
        int arraySize = movesAL.size();
        String[] moves = new String[arraySize];

        for (int i = 0; i < arraySize; i++) {
            String move = movesAL.get(i).getPosition() + "-" + movesAL.get(i).getMove() + "-" + movesAL.get(i).getPlayerID();
            moves[i] = move;
        }
        return moves;
    }


    public void leaveNetwork(ServerHandler serverHandler){
        clients.remove(serverHandler);
        System.out.println("left chat");
    }


    public void close(DataInputStream bufferedReader, DataOutputStream bufferedWriter)
    {
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("type", "server_closed");
        players.forEach((id, handler) -> {
            try {
                handler.dataOutputStream.writeUTF(responseObject.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
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

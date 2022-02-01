import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import Models.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;

public class ServerHandler extends Thread {

    private boolean running;

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
                            responseObject.addProperty("successful", "false");
                            //Send responseObject to client
                        } else {
                            System.out.println(player.getId());
                            responseObject.addProperty("successful", "true");
                            responseObject.addProperty("id", player.getId());
                            responseObject.addProperty("username", player.getUsername());
                            responseObject.addProperty("score", player.getScore());
                            responseObject.addProperty("wins", player.getWins());
                            responseObject.addProperty("losses", player.getLosses());
                            //send responseObject to client
                        }
                        break;

                    case "logout":

                        logout(requestObject);
                        break;

                    case "signup":

                        if(signup(requestObject)) {
                            responseObject.addProperty("successful", "true");
                            //Show login Scene
                        } else {
                            responseObject.addProperty("successful", "false");
                            //Respond to client with sign up error
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

                    case "finish_game":

                        finishGame(requestObject);
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

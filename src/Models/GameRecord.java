package Models;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class GameRecord {
    private int gameID;
    private int playerID;
    private int stepNumber;
    private int move;
    private int position;

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public int getMove() {
        return move;
    }

    public void setMove(int move) {
        this.move = move;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    //    public int getPlayerXID() {
//        return playerXID;
//    }
//
//    public void setPlayerXID(int playerXID) {
//        this.playerXID = playerXID;
//    }
//
//    public int getPlayerOID() {
//        return playerOID;
//    }
//
//    public void setPlayerOID(int playerOID) {
//        this.playerOID = playerOID;
//    }
//
//    public int getStepNumber() {
//        return stepNumber;
//    }
//
//    public void setStepNumber(int stepNumber) {
//        this.stepNumber = stepNumber;
//    }
//
//    public int[] getStep() {
//        return step;
//    }
//
//    public void setStep(int[] step) {
//        this.step = step;
//    }

    //Should keep track of step number in controller, because we removed auto increment in step number in database

    public GameRecord create(int gameID, int playerID, int move, int position) {
        ConnectDB connectDB = new ConnectDB();
        GameRecord gameRecord = new GameRecord();

        String sql = "insert into moves (game_id, player_id, sign, pos) values (?, ?, ?, ?)";

        try (Connection con = connectDB.getConnection(); PreparedStatement st = con.prepareStatement(sql);) {

            //st.setInt(1, stepNumber);
            st.setInt(1, gameID);
            st.setInt(2, playerID);
            st.setInt(3, move);
            st.setInt(4, position);

            st.executeUpdate();
            gameRecord.setGameID(gameID);
            gameRecord.setStepNumber(stepNumber);
            gameRecord.setMove(move);
            gameRecord.setPlayerID(playerID);
            gameRecord.setPosition(position);

        } catch (SQLException e) {
            e.printStackTrace();
            gameRecord = null;
        }

        return gameRecord;
    }

    public ArrayList<GameRecord> findByGameID(int id) {

        ConnectDB connectDB = new ConnectDB();

        String sql = "select * from moves where game_id = ?";

        GameRecord gameRecord = null;

        ArrayList<GameRecord> moves = new ArrayList<>();

        try (Connection con = connectDB.getConnection(); PreparedStatement st = con.prepareStatement(sql)) {

            st.setInt(1, id);
            ResultSet rs = st.executeQuery();

            gameRecord = new GameRecord();

            while (rs.next()) {
                gameRecord = new GameRecord();
                gameRecord.setGameID(rs.getInt(2));
                gameRecord.setPlayerID(rs.getInt(3));
                gameRecord.setMove(rs.getInt(4));
                gameRecord.setPosition(rs.getInt(5));
                moves.add(gameRecord);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return moves;
    }

//    public void save() {
//        ConnectDB connectDB = new ConnectDB();
//
//        String sql = "update moves set step_number = ?, move = ? where game_id = ? and player_x = ? and player_o = ?";
//
//        try (Connection con = connectDB.getConnection(); PreparedStatement st = con.prepareStatement(sql);) {
//
//            st.setInt(1, this.stepNumber);
//            st.setInt(3, this.gameID);
//            st.executeUpdate();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

//    public ArrayList<GameRecord> findByPlayer(String username) {
//
//        ConnectDB connectDB = new ConnectDB();
//
//        ArrayList<GameRecord> gameRecords = new ArrayList<>();
//
//        int playerID = -1;
//
//        String sql1 = "select player_id from player where username = ?";
//
//        PreparedStatement st2 = null;
//
//        ResultSet rs1 = null;
//
//        ResultSet rs2 = null;
//
//        try (Connection con = connectDB.getConnection(); PreparedStatement st1 = con.prepareStatement(sql1);) {
//
//            st1.setString(1, username);
//
//            rs1 = st1.executeQuery();
//
//            while(rs1.next()) {
//                playerID = rs1.getInt(1);
//            }
//
//            if(playerID != -1) {
//
//                String sql2 = "select * from play where player_x = ? or player_o ?";
//
//                st2 = con.prepareStatement(sql2);
//
//                st2.setInt(1, playerID);
//                st2.setInt(2, playerID);
//
//                rs2 = st2.executeQuery();
//
//                while (rs2.next()) {
//                    GameRecord gameRecord = new GameRecord();
//
//                    gameRecord.setStepNumber(rs2.getInt(1));
//                    //https://stackoverflow.com/questions/7646392/convert-string-to-int-array-in-java/25839398
//                    String arrayAsString = rs2.getString(2);
//                    String[] stringArray = arrayAsString.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
//                    int[] intArray = new int[stringArray.length];
//                    for (int i = 0; i < stringArray.length; i++) {
//                        try {
//                            intArray[i] = Integer.parseInt(stringArray[i]);
//                        } catch (NumberFormatException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    gameRecord.setStep(intArray);
//                    gameRecord.setGameID(rs2.getInt(3));
//                    gameRecord.setPlayerXID(rs2.getInt(4));
//                    gameRecord.setPlayerOID(rs2.getInt(5));
//
//                    gameRecords.add(gameRecord);
//                }
//
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (st2 != null) {
//                    st2.close();
//                }
//                if (rs1 != null) {
//                    rs1.close();
//                }
//                if (rs2 != null) {
//                    rs2.close();
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return gameRecords;
//    }
//
//    public ArrayList<GameRecord> findByPlayers(String player1, String player2) {
//
//        ConnectDB connectDB = new ConnectDB();
//
//        ArrayList<GameRecord> gameRecords = new ArrayList<>();
//
//        int player1ID = -1;
//
//        int player2ID = -1;
//
//        String sql1 = "select player_id from player where username = ?";
//
//        PreparedStatement st2 = null;
//
//        ResultSet rs1 = null;
//
//        ResultSet rs2 = null;
//
//        try (Connection con = connectDB.getConnection(); PreparedStatement st1 = con.prepareStatement(sql1);) {
//
//            st1.setString(1, player1);
//
//            rs1 = st1.executeQuery();
//
//            while(rs1.next()) {
//                player1ID = rs1.getInt(1);
//            }
//
//            st1.setString(1, player2);
//
//            rs1 = st1.executeQuery();
//
//            while(rs1.next()) {
//                player2ID = rs1.getInt(1);
//            }
//
//            if(player1ID != -1 && player2ID != -1) {
//
//                String sql2 = "select * from play where (player_x = ? and player_o ?) or (player_x = ? and player_o ?)";
//
//                st2 = con.prepareStatement(sql2);
//
//                st2.setInt(1, player1ID);
//                st2.setInt(2, player2ID);
//
//                st2.setInt(3, player2ID);
//                st2.setInt(4, player1ID);
//
//                rs2 = st2.executeQuery();
//
//                while (rs2.next()) {
//                    GameRecord gameRecord = new GameRecord();
//
//                    gameRecord.setStepNumber(rs2.getInt(1));
//                    //https://stackoverflow.com/questions/7646392/convert-string-to-int-array-in-java/25839398
//                    String arrayAsString = rs2.getString(2);
//                    String[] stringArray = arrayAsString.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
//                    int[] intArray = new int[stringArray.length];
//                    for (int i = 0; i < stringArray.length; i++) {
//                        try {
//                            intArray[i] = Integer.parseInt(stringArray[i]);
//                        } catch (NumberFormatException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    gameRecord.setStep(intArray);
//                    gameRecord.setGameID(rs2.getInt(3));
//                    gameRecord.setPlayerXID(rs2.getInt(4));
//                    gameRecord.setPlayerOID(rs2.getInt(5));
//
//                    gameRecords.add(gameRecord);
//                }
//
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (st2 != null) {
//                    st2.close();
//                }
//                if (rs1 != null) {
//                    rs1.close();
//                }
//                if (rs2 != null) {
//                    rs2.close();
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return gameRecords;
//    }
    
}

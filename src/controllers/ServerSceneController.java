package controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import server_ui.ServerScene;

public class ServerSceneController {

    private ServerScene serverScene;
    private Server server;
    private Button startBtn1;
    private Button stopBtn1;
    private boolean clicked = false;

// t3ref el columns   11111111111111
//    public TableView<Player> users_table;
//    public TableColumn<Player, String> username;
    ///
//    public ObservableList<Player> getOnlineUsers() {
//        ObservableList<Player> list = FXCollections.observableArrayList();
//        // get user bring observable list        222222
//        Player player1 = new Player();
//        List<Player> players =  player1.findOnlinePlayers();
//        for (Player player : players) {
//            list.add(player);
//        }
//        return list;
//    }


    //    Server server2 = new Server();
//    Button startBtn, Button stopBtn
    public ServerSceneController(ServerScene serverScene1, Stage primaryStage) {

        serverScene = serverScene1;
//        startBtn1=startBtn;
//        stopBtn1=stopBtn;
        serverScene1.startServerBtnHandler(serverStart(primaryStage));
        serverScene1.stopServerBtnHandler(serverStop(primaryStage));
    }

    private EventHandler<ActionEvent> serverStart(Stage primaryStage) {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("start server before new server()");
                if (!clicked) {
                    new Server();
                    clicked = true;
//                    username.setCellValueFactory(new PropertyValueFactory<Player, String>("username"));
//                    users_table.setItems(getOnlineUsers());
                    System.out.println("start server after new server()");
                }
            }
        };
    }

    private EventHandler<ActionEvent> serverStop(Stage primaryStage) {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (clicked) {
                    Server.close();
                    clicked = false;

                    System.out.println("close server");
                }
            }
        };
    }


}

package controllers;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.Duration;
import server_ui.ServerScene;

public class ServerSceneController {

    private ServerScene serverScene;
    private Server server;
    private Button startBtn1;
    private Button stopBtn1;
    private boolean clicked = false;
    Button  refreshbtnn;



    public ServerSceneController(ServerScene serverScene1, Stage primaryStage,TableView table,Button refreshServerBtn) {
        refreshbtnn = refreshServerBtn;
        serverScene = serverScene1;
//        startBtn1=startBtn;
//        stopBtn1=stopBtn;
        serverScene1.startServerBtnHandler(serverStart(primaryStage));
        serverScene1.stopServerBtnHandler(serverStop(primaryStage));
        serverScene1.refreshServerBtnHandler(serverRefresh(primaryStage,serverScene1,table));
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> {
           // System.out.println("hahahahahaah");
        });
        pause.playFromStart();

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

    private EventHandler<ActionEvent> serverRefresh(Stage primaryStage,ServerScene serverScene1,TableView table) {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Refreshed1111");
                 table.setItems(serverScene1.getOnlineUsers());
                table.refresh();
                 System.out.println("Refreshed");
            }
        };
    }

//   public Button refreshBtn(){
//        return  refreshbtnn;
//   }
}



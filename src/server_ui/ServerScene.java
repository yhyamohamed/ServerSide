package server_ui;

import Models.Player;
import controllers.ServerSceneController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.awt.*;
import java.util.List;

public class ServerScene extends AnchorPane {

    public Button stopServerBtn;
    public Button startServerBtn;
    public Button refreshServerBtn;
    public Label title;
    private TableView<Player> table;
    private TableColumn<Player, String> tableColumn_username;
    private TableColumn<Player, String> tableColumn_password;
    private TableColumn<Player, Boolean> tableColumn_online;
    private TableColumn<Player, Integer> tableColumn_losses;
    private TableColumn<Player, Integer> tableColumn_wins;

    public ServerScene(Stage primaryStage) {


        stopServerBtn = new Button();
        startServerBtn = new Button();
        refreshServerBtn = new Button();

        table = new TableView<Player>();
//      table.setId("users_table");
        tableColumn_username = new TableColumn<Player, String>("username");
        tableColumn_username.setId("username");
//        tableColumn_username.setStyle("/server_ui/Resources/styles.css");
        tableColumn_username.setPrefWidth(111.6);

        tableColumn_password = new TableColumn<Player, String>("password");
        tableColumn_password.setId("hashedPassword");
        tableColumn_password.setPrefWidth(111.6);



        tableColumn_online = new TableColumn<Player, Boolean>("Online");
        tableColumn_online.setPrefWidth(111.6);
        tableColumn_losses = new TableColumn<Player, Integer>("losses");
        tableColumn_losses.setPrefWidth(111.6);
        tableColumn_wins = new TableColumn<Player, Integer>("wins");
        tableColumn_wins.setPrefWidth(111.6);

        table.setItems(getOnlineUsers());

        tableColumn_username.setCellValueFactory(new PropertyValueFactory<Player, String>("username"));
        tableColumn_password.setCellValueFactory(new PropertyValueFactory<Player, String>("hashedPassword"));
        tableColumn_online.setCellValueFactory(new PropertyValueFactory<Player, Boolean>("online"));
        tableColumn_losses.setCellValueFactory(new PropertyValueFactory<Player, Integer>("losses"));
        tableColumn_wins.setCellValueFactory(new PropertyValueFactory<Player, Integer>("wins"));


        table.getColumns().addAll(tableColumn_username, tableColumn_password, tableColumn_online, tableColumn_losses, tableColumn_wins);


        title = new Label();

        setPrefHeight(400.0);
        setPrefWidth(600.0);
        getStyleClass().add("background");
        getStylesheets().add("/server_ui/Resources/styles.css");

        table.setLayoutX(20.0);
        table.setLayoutY(120.0);
        table.setMinHeight(250);
        table.setMinWidth(558);
        VBox vbox = new VBox();
        vbox.setSpacing(20);



//
        startServerBtn.setLayoutX(20.0);
        startServerBtn.setLayoutY(60.0);
        startServerBtn.setMnemonicParsing(false);
        startServerBtn.setPrefHeight(50.0);
        startServerBtn.setPrefWidth(150.0);
        startServerBtn.getStyleClass().add("stop_server_button");
        startServerBtn.getStylesheets().add("/server_ui/Resources/styles.css");
        startServerBtn.setText("Start server");

        stopServerBtn.setLayoutX(200.0);
        stopServerBtn.setLayoutY(60.0);
        stopServerBtn.setMnemonicParsing(false);
        stopServerBtn.setPrefHeight(50.0);
        stopServerBtn.setPrefWidth(150.0);
        stopServerBtn.getStyleClass().add("start_server_button");
        stopServerBtn.getStylesheets().add("/server_ui/Resources/styles.css");
        stopServerBtn.setText("Stop server");
        stopServerBtn.setVisible(true);

        refreshServerBtn.setLayoutX(380.0);
        refreshServerBtn.setLayoutY(60.0);
        refreshServerBtn.setMnemonicParsing(false);
        refreshServerBtn.setPrefHeight(50.0);
        refreshServerBtn.setPrefWidth(150.0);
        refreshServerBtn.getStyleClass().add("stop_server_button");
        refreshServerBtn.getStylesheets().add("/server_ui/Resources/styles.css");
        refreshServerBtn.setText("Refresh");
        refreshServerBtn.setVisible(true);


        title.setLayoutX(170.0);
        title.setLayoutY(5.0);
        title.setPrefHeight(58.0);
        title.setPrefWidth(374.0);
        title.getStyleClass().add("logo");
        title.setText("Game Server");
        title.setTextFill(javafx.scene.paint.Color.valueOf("#dbe2e5"));
        title.setFont(new Font("System Bold Italic", 30.0));

        table.setMaxSize(350, 200);

//        vbox.getChildren().add(table);
        getChildren().add(stopServerBtn);
        getChildren().add(startServerBtn);
        getChildren().add(refreshServerBtn);
        getChildren().add(title);
        getChildren().add(table);



        new ServerSceneController(this, primaryStage,table,refreshServerBtn);

    }

    public ObservableList<Player> getOnlineUsers() {
        ObservableList<Player> list = FXCollections.observableArrayList();
        // get user bring observable list        222222
        Player player1 = new Player();
        List<Player> players = player1.findAllPlayers();
        for (Player player : players) {
            list.add(player);
        }
        return list;
    }

// fetch player done
// injiect el data gwa el table done




    public void startServerBtnHandler(EventHandler<ActionEvent> Action) {
        startServerBtn.setOnAction(Action);
    }

    public void stopServerBtnHandler(EventHandler<ActionEvent> Action) {
        stopServerBtn.setOnAction(Action);
    }
    public void refreshServerBtnHandler(EventHandler<ActionEvent> Action) {
        refreshServerBtn.setOnAction(Action);
    }




}

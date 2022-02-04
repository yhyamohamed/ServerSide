package server_ui;

import controllers.ServerSceneController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ServerScene extends AnchorPane {

    public final Button stopServerBtn;
    public final Button startServerBtn;
    public final Label title;

    public ServerScene(Stage primaryStage) {

        stopServerBtn = new Button();
        startServerBtn = new Button();
        title = new Label();

        setPrefHeight(400.0);
        setPrefWidth(600.0);
        getStyleClass().add("background");
        getStylesheets().add("/server_ui/Resources/styles.css");

        stopServerBtn.setLayoutX(100.0);
        stopServerBtn.setLayoutY(180.0);
        stopServerBtn.setMnemonicParsing(false);
        stopServerBtn.setPrefHeight(50.0);
        stopServerBtn.setPrefWidth(150.0);
        stopServerBtn.getStyleClass().add("start_server_button");
        stopServerBtn.getStylesheets().add("/server_ui/Resources/styles.css");
        stopServerBtn.setText("Stop server");
        stopServerBtn.setVisible(true);

        startServerBtn.setLayoutX(350.0);
        startServerBtn.setLayoutY(180.0);
        startServerBtn.setMnemonicParsing(false);
        startServerBtn.setPrefHeight(50.0);
        startServerBtn.setPrefWidth(150.0);
        startServerBtn.getStyleClass().add("stop_server_button");
        startServerBtn.getStylesheets().add("/server_ui/Resources/styles.css");
        startServerBtn.setText("Start server");

        title.setLayoutX(170.0);
        title.setLayoutY(35.0);
        title.setPrefHeight(58.0);
        title.setPrefWidth(374.0);
        title.getStyleClass().add("logo");
        title.setText("Game Server");
        title.setTextFill(javafx.scene.paint.Color.valueOf("#dbe2e5"));
        title.setFont(new Font("System Bold Italic", 38.0));

        getChildren().add(stopServerBtn);
        getChildren().add(startServerBtn);
        getChildren().add(title);

        new ServerSceneController(this, primaryStage);

    }

    public void startServerBtnHandler(EventHandler<ActionEvent> Action) {
        startServerBtn.setOnAction(Action);
    }

    public void stopServerBtnHandler(EventHandler<ActionEvent> Action) {
        stopServerBtn.setOnAction(Action);
    }
}

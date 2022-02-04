/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import server_ui.ServerScene;


public class EntryScreen extends Application {
    @Override
    public void start(Stage primaryStage) {
// not resizable
        primaryStage.setResizable(false);
// create scene
        ServerScene root = new ServerScene(primaryStage);
        Scene scene = new Scene(root);
        scene.getStylesheets().add("server_ui/Resources/styles.css");
        primaryStage.setTitle("server screen");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}

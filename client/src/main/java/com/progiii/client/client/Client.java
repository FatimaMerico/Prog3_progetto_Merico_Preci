package com.progiii.client.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Client extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("reply.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 300);
        stage.setTitle("CatMail");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        System.out.println("FXML Path: " + Client.class.getResource("/com/progiii/client/client/reply.fxml"));

        launch();
    }
}
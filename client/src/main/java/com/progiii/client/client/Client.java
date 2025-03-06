package com.progiii.client.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Client extends Application {

    private static Stage primaryStage;
    private static String userEmail;

    public static void setUserEmail(String email) {
        userEmail = email;
    }

    public static String getUserEmail() {
        return userEmail;
    }

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        showLoginScene();
    }

    public static void showLoginScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 200);
        primaryStage.setTitle("CatMail - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showInboxScene(String userEmail) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("inbox.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        InboxController controller = fxmlLoader.getController();
        controller.setUserEmail(userEmail);
        primaryStage.setTitle("CatMail - Inbox");
        primaryStage.setScene(scene);
    }

    public static void showMessageScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("message.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 300);
        primaryStage.setTitle("CatMail - Nuovo Messaggio");
        primaryStage.setScene(scene);
    }

    public static void showReplyScene(String sender, String subject, String message) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("reply.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 300);
        ReplyController controller = fxmlLoader.getController();
        controller.setupReply(sender, subject, message);
        primaryStage.setTitle("CatMail - Rispondi");
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch();
    }
}
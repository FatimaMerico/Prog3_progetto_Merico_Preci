package com.progiii.client.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Classe principale dell'app client
 * Gestisce l'interfaccia utente e le interazioni con il server
 */
public class Client extends Application {

    private static Stage primaryStage;
    /**
     * Metodo principale dell'applicazione
     * @param stage stage principale
     * @throws IOException se si verifica un errore durante il caricamento della scena
     */
    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        showLoginScene();
    }
    /**
     * Mostra la scena di login
     * @throws IOException se si verifica un errore durante il caricamento della scena
     */
    public static void showLoginScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 200);
        primaryStage.setTitle("CatMail - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    /**
     * Mostra la scena della inbox
     * @param userEmail l'email dell'utente
     * @throws IOException se si verifica un errore durante il caricamento della scena
     */
    public static void showInboxScene(String userEmail) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("inbox.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        InboxController controller = fxmlLoader.getController();
        controller.setUserEmail(userEmail);
        primaryStage.setTitle("CatMail - Inbox");
        primaryStage.setScene(scene);
    }
    /**
     * Mostra la scena del messaggio
     * @param userEmail l'email dell'utente
     * @throws IOException se si verifica un errore durante il caricamento della scena
     */
    public static void showMessageScene(String userEmail) throws IOException {
        FXMLLoader loader = new FXMLLoader(Client.class.getResource("message.fxml"));
        Scene scene = new Scene(loader.load());

        MessageController controller = loader.getController();
        controller.setUserEmail(userEmail); // Imposta l'email dell'utente

        primaryStage.setScene(scene);
    }

    /**
     * Metodo principale che lancia il client
     * @param args argomenti della riga di comando
     */
    public static void main(String[] args) {
        launch();
    }
}
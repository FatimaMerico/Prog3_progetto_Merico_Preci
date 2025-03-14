package com.progiii.demo.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
/**
 * Classe principale del server
 * Avvia l'interfaccia grafica del server
 */
public class Server extends Application {
    /**
     * Metodo di avvio del server
     * @param stage lo stage principale
     * @throws IOException se si verifica un errore durante il caricamento della scena
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Server.class.getResource("server.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);
        stage.setTitle("Mail Server");
        stage.setScene(scene);
        stage.show();
    }
    /**
     * Metodo principale che avvia l'applicazione
     * @param args gli argomenti della riga di comando
     */
    public static void main(String[] args) {
        launch();
    }
}

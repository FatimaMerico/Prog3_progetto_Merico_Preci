package com.progiii.demo.demo;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
/**
 * Controller per l'interfaccia grafica del server
 * Gestisce l'avvio e l'arresto del server
 */
public class ServerController {
    @FXML private Button startButton;
    @FXML private Button stopButton;
    @FXML private ListView<String> logListView;
    private MailServer mailServer;

    /**
     * Avvia il server (parte grafica)
     */
    @FXML
    protected void startServer() {
        mailServer = new MailServer(this);
        new Thread(mailServer).start();
        logMessage("Server avviato...");
        startButton.setDisable(true);
        stopButton.setDisable(false);
    }
    /**
     * Arresta il server (parte grafica)
     */
    @FXML
    protected void stopServer() {
        if (mailServer != null) {
            mailServer.stop();
            logMessage("Server arrestato.");
            startButton.setDisable(false);
            stopButton.setDisable(true);
        }
    }
    /**
     * Aggiunge un messaggio al log
     * @param message il messaggio da aggiungere
     */
    public void logMessage(String message) {
        Platform.runLater(() -> logListView.getItems().add(message));
    }
}

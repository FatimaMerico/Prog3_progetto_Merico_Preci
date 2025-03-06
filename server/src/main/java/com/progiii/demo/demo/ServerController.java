package com.progiii.demo.demo;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class ServerController {
    @FXML private Button startButton;
    @FXML private Button stopButton;
    @FXML private ListView<String> logListView;

    private MailServer mailServer;

    @FXML
    protected void startServer() {
        mailServer = new MailServer(this);
        new Thread(mailServer).start();
        logMessage("Server avviato...");
    }

    @FXML
    protected void stopServer() {
        if (mailServer != null) {
            mailServer.stop();
            logMessage("Server arrestato.");
        }
    }

    public void logMessage(String message) {
        Platform.runLater(() -> logListView.getItems().add(message));
    }
}

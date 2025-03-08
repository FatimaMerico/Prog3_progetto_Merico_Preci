package com.progiii.client.client;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class InboxController {
    @FXML private Label userMail; // Mostra l'email dell'utente
    @FXML private Label connectionStatus;
    @FXML private TableView<JSONObject> emailTable;
    @FXML private TableColumn<JSONObject, String> senderColumn;
    @FXML private TableColumn<JSONObject, String> subjectColumn;
    @FXML private TableColumn<JSONObject, String> previewColumn;
    @FXML private Button sendButton;
    @FXML private Button deleteButton;

    private ObservableList<JSONObject> emailList;
    private ScheduledExecutorService scheduler;

    public InboxController() {
        emailList = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        // Collega le colonne ai dati JSON
        senderColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getString("sender")));
        subjectColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getString("subject")));
        previewColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getString("body")));

        emailTable.setItems(emailList);

        // Avvia il controllo del server ogni 2 secondi
        startServerCheck();
    }

    private void startServerCheck() {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            boolean serverOnline = checkServerConnection("localhost", 12345); // Cambia IP e porta se necessario
            Platform.runLater(() -> updateUI(serverOnline));
        }, 0, 2, TimeUnit.SECONDS);
    }

    private boolean checkServerConnection(String host, int port) {
        try (Socket socket = new Socket(host, port)) {
            return true; // Connessione riuscita
        } catch (IOException e) {
            return false; // Connessione fallita
        }
    }

    private void updateUI(boolean serverOnline) {
        if (serverOnline) {
            connectionStatus.setText("Server Connesso");
            connectionStatus.setStyle("-fx-text-fill: green;");
            sendButton.setDisable(false);
            deleteButton.setDisable(false);
        } else {
            connectionStatus.setText("Server NON Connesso");
            connectionStatus.setStyle("-fx-text-fill: red;");
            sendButton.setDisable(true);
            deleteButton.setDisable(true);
        }
    }

    public void stopServerCheck() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }
    public void setUserEmail(String email) {
        userMail.setText(email);
    }

    public void loadEmails(ObservableList<String> emailData) {
        emailList.clear(); // Pulisce la lista esistente
        for (String emailJson : emailData) {
            emailList.add(new JSONObject(emailJson)); // Aggiunge ogni email come JSONObject
        }
    }

    public void addEmail(String emailJson) {
        emailList.add(new JSONObject(emailJson)); // Aggiunge una nuova email dinamicamente
    }

    @FXML
    private void HandleScrivi() throws IOException {
        Client.showMessageScene(userMail.getText()); // Passa l'email corrente
    }

    @FXML
    private void HandleLogout() throws IOException {
        Client.showLoginScene();
    }

    @FXML
    private void HandleElimina() throws IOException {
        System.out.println("Eliminazione email");
    }

}

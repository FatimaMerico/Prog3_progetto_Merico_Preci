package com.progiii.client.client;

import com.progiii.client.client.utils.NotificationManager;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class InboxController {
    @FXML private Label userMail;
    @FXML private Label connectionStatus;
    @FXML private TableView<JSONObject> emailTable;
    @FXML private TableColumn<JSONObject, String> senderColumn;
    @FXML private TableColumn<JSONObject, String> subjectColumn;
    @FXML private TableColumn<JSONObject, String> previewColumn;
    @FXML private Button sendButton;
    @FXML private Button deleteButton;
    @FXML private Label notificationLabel; // Riferimento al Label per la notifica

    private static ObservableList<JSONObject> emailList = FXCollections.observableArrayList();
    private ScheduledExecutorService scheduler;

    public InboxController() {
        if (emailList == null) {
            emailList = FXCollections.observableArrayList();
        }
    }

    @FXML
    public void initialize() {
        senderColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getString("sender")));
        subjectColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getString("subject")));
        previewColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getString("body")));

        emailTable.setItems(emailList);
        startServerCheck();

        //Aggiungiamo il Mouse Listener alla TableView
        emailTable.setRowFactory(tv -> {
            TableRow<JSONObject> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    JSONObject selectedEmail = row.getItem();
                    //Singolo click: abilita il pulsante di eliminazione
                    if (event.getClickCount() == 1) {
                        deleteButton.setDisable(false);
                    }
                    //Doppio click: apre la scena di risposta
                    if (event.getClickCount() == 2) {
                        openReplyScene(selectedEmail);
                    }
                }
            });
            return row;
        });
        //Ottieni l'istanza del NotificationManager
        NotificationManager notificationManager = NotificationManager.getInstance();
        //Osserva il NotificationManager per aggiornare la notifica
        notificationManager.notificationMessageProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        notificationLabel.setText(newValue);
                        notificationLabel.setVisible(true);
                    } else {
                        notificationLabel.setVisible(false);
                    }
                }
        );
        // Ripristina la notifica se è attiva
        if (notificationManager.isNotificationActive()) {
            notificationLabel.setText(notificationManager.notificationMessageProperty().get());
            notificationLabel.setVisible(true);
        }
    }

    private void startServerCheck() {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            boolean serverOnline = checkServerConnection("localhost", 3000);
            Platform.runLater(() -> updateUI(serverOnline));
            if (serverOnline) fetchEmails();
        }, 0, 1, TimeUnit.SECONDS);
    }

    private boolean checkServerConnection(String host, int port) {
        try (Socket socket = new Socket(host, port)) {
            emailTable.refresh();
            return true;
        } catch (IOException e) {
            return false;
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
            sendButton.setDisable(false);
            deleteButton.setDisable(true);
        }
    }

    private void fetchEmails() {
        try (Socket socket = new Socket("localhost", 3000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner in = new Scanner(socket.getInputStream())) {

            // Creazione della richiesta PING sotto forma di JSON
            JSONObject pingRequest = new JSONObject();
            pingRequest.put("type", "PING");
            pingRequest.put("sender", userMail.getText());
            // Invio della richiesta PING al server
            out.println(pingRequest);
            // Lettura della risposta dal server (Lista di email)
            if (in.hasNextLine()) {
                String response = in.nextLine();
                Platform.runLater(() -> handleServerResponse(response));
            }
        } catch (IOException e) {
            System.out.println("Errore ricezione email: " + e.getMessage());
        }
    }

    private void handleServerResponse(String response) {
        try {
            if (response.trim().isEmpty()) {
                System.out.println("La risposta del server è vuota.");
                return;
            }
            // Verifica se la risposta inizia con '[' (indica un JSONArray)
            if (response.trim().startsWith("[")) {
                JSONArray emailArray = new JSONArray(response);
                for (int i = 0; i < emailArray.length(); i++) {
                    JSONObject email = emailArray.getJSONObject(i);
                    emailList.add(email); // Aggiunge direttamente tutte le email ricevute
                    // Notifica il NotificationManager
                    NotificationManager.getInstance().showNotification("Nuova mail ricevuta!");
                }
                emailTable.refresh(); // Aggiorna la vista della tabella
            }
        } catch (Exception e) {
            System.out.println("Errore nella gestione della risposta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void HandleScrivi() throws IOException {
        Client.showMessageScene(userMail.getText());//Dopo aver inviato la mail, vogliamo ricaricare le email
    }

    @FXML
    private void HandleLogout() throws IOException {
        emailList.clear();
        Client.showLoginScene();
    }

    @FXML
    private void HandleElimina() {
        JSONObject selectedEmail = emailTable.getSelectionModel().getSelectedItem();
        if (selectedEmail == null) {
            System.out.println("Nessuna email selezionata per l'eliminazione.");
            return;
        }
        try (Socket socket = new Socket("localhost", 3000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Creiamo l'oggetto JSON per la richiesta
            JSONObject deleteRequest = new JSONObject();
            deleteRequest.put("type", "ELIMINA");
            deleteRequest.put("richiedente", userMail.getText());

            // Creiamo un oggetto per l'email da eliminare
            JSONObject emailData = new JSONObject();
            emailData.put("sender", selectedEmail.getString("sender"));
            emailData.put("subject", selectedEmail.getString("subject"));
            emailData.put("body", selectedEmail.getString("body"));
            deleteRequest.put("email_da_cancellare", emailData);

            // Invia il JSON al server
            out.println(deleteRequest);
            System.out.println("Richiesta di eliminazione inviata: " + deleteRequest);

            // Leggi la risposta del server
            String response = in.readLine();
            if (response != null) {
                System.out.println("Risposta del server: " + response);  // Stampa la risposta per il debug
                if (response.contains("SUCCESS")) {
                    // Se la risposta è positiva, rimuovi l'email dalla lista (UI)
                    emailTable.getItems().remove(selectedEmail);
                    System.out.println("Email eliminata con successo.");
                } else {
                    System.out.println("Errore nell'eliminazione dell'email.");
                }
            } else {
                System.out.println("Nessuna risposta dal server.");
            }
        } catch (IOException e) {
            System.out.println("Errore nella comunicazione con il server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void openReplyScene(JSONObject email) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("reply.fxml"));
            Parent root = loader.load();

            //Prendiamo il controller della nuova scena
            ReplyController replyController = loader.getController();

            //Passare i dettagli della mail selezionata alla Reply Scene
            replyController.setReplyFields(email);
            replyController.setUserEmail(userMail.getText());

            //Mostra la nuova scena
            Stage stage = (Stage) emailTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Errore nel caricamento della scena di risposta.");
        }
    }

    public void setUserEmail(String email) {
        userMail.setText(email);
    }
}

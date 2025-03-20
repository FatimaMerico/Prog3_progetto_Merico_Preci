package com.progiii.client.client.controllers;

import com.progiii.client.client.Client;
import com.progiii.client.client.models.InboxModel;
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
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
/**
 * Controller per la scena della inbox
 * Gestisce la visualizzazione delle email e le interazioni con il server
 */
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
    private InboxModel inboxModel;
    /**
     * Costruttore della classe InboxController
     */
    public InboxController() {
        this.inboxModel = new InboxModel();
        if (emailList == null) {
            emailList = FXCollections.observableArrayList();
        }
    }
    /**
     * Metodo di inizializzazione della scena
     */
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
    /**
     * Avvia il controllo periodico dello stato del server
     */
    private void startServerCheck() {
        scheduler = Executors.newScheduledThreadPool(3);
        scheduler.scheduleAtFixedRate(() -> {
            boolean serverOnline = checkServerConnection("localhost", 3000);
            Platform.runLater(() -> updateUI(serverOnline));
            if (serverOnline) fetchEmails();
        }, 0, 1, TimeUnit.SECONDS);
    }
    /**
     * Controlla la connessione al server
     * @param host l'host del server
     * @param port la porta del server
     * @return true se il server è online, false altrimenti
     */
    private boolean checkServerConnection(String host, int port) {
        try (Socket socket = new Socket(host, port)) {
            emailTable.refresh();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    /**
     * Aggiorna l'interfaccia utente in base allo stato del server
     * @param serverOnline true se il server è online, false altrimenti
     */
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
    /**
     * Recupera le email dal server
     */
    private void fetchEmails() {
        String response = inboxModel.fetchEmails(userMail.getText());
        Platform.runLater(() -> handleServerResponse(response));
    }

    /**
     * Gestisce la risposta del server
     * @param response la risposta del server
     */
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
    /**
     * Gestisce l'azione del pulsante "Scrivi"
     * @throws IOException se si verifica un errore durante il caricamento della scena
     */
    @FXML
    private void HandleScrivi() throws IOException {
        Client.showMessageScene(userMail.getText());//Dopo aver inviato la mail, vogliamo ricaricare le email
    }
    /**
     * Gestisce l'azione del pulsante "Logout"
     * @throws IOException se si verifica un errore durante il caricamento della scena
     */
    @FXML
    private void HandleLogout() throws IOException {
        emailList.clear();
        Client.showLoginScene();
    }
    /**
     * Gestisce l'azione del pulsante "Elimina"
     * @trows IOException se si verifica un errore durante la comunicazione con il server
     */
    @FXML
    private void HandleElimina() {
        JSONObject selectedEmail = emailTable.getSelectionModel().getSelectedItem();
        if (selectedEmail == null) {
            System.out.println("Nessuna email selezionata per l'eliminazione.");
            return;
        }
        boolean success = inboxModel.deleteEmails(userMail.getText(), selectedEmail.getInt("id"));
        if (success) {
            emailTable.getItems().remove(selectedEmail);
        }
    }
    /**
     * Apre la scena di risposta
     * @param email l'email selezionata
     */
    @FXML
    private void openReplyScene(JSONObject email) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/progiii/client/client/reply.fxml"));
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
    /**
     * Imposta l'email dell'utente
     * @param email l'email dell'utente
     */
    public void setUserEmail(String email) {
        userMail.setText(email);
    }
}

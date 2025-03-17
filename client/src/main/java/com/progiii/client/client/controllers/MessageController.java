package com.progiii.client.client.controllers;

import com.progiii.client.client.Client;
import com.progiii.client.client.models.MessageModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import org.json.JSONObject;
/**
 * Controller per la scena del messaggio
 * Gestisce la composizione e l'invio delle email
 */
public class MessageController {
    @FXML private TextField toField;
    @FXML private TextField subjectField;
    @FXML private TextArea messageBody;
    @FXML private Label statusLabel;

    private String userEmail;
    private MessageModel messageModel = new MessageModel();
    /**
     * Imposta l'email dell'utente
     * @param email l'email dell'utente
     */
    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    /**
     * Imposta i campi del messaggio
     * @param sender il mittente
     * @param recipients i destinatari
     * @param subject l'oggetto
     * @param message il corpo del messaggio
     */
    public void setFields(String sender, String recipients, String subject, String message) {
        this.userEmail = sender;
        toField.setText(recipients);
        subjectField.setText(subject);
        messageBody.setText(message);
    }

    /**
     * Gestisce l'azione del pulsante "Indietro"
     * @throws IOException se si verifica un errore durante il caricamento della scena
     */
    @FXML
    private void HandleIndietro() throws IOException {
        Client.showInboxScene(userEmail);
    }
    /**
     * Gestisce l'azione del pulsante "Invia"
     */
    @FXML
    private void HandleInvia() throws IOException {
        JSONObject email = new JSONObject();
        email.put("type", "EMAIL");
        email.put("sender", userEmail);
        email.put("receiver", toField.getText());
        email.put("subject", subjectField.getText());
        email.put("body", messageBody.getText());
        email.put("timestamp", java.time.LocalDateTime.now().toString());
        email.put("status", "DA LEGGERE");

        // Genera un ID univoco per l'email
        int emailId = email.toString().hashCode();
        email.put("id", emailId);

        boolean success = messageModel.sendEmail(email, statusLabel);
        if (success) {
            System.out.println("Email inviata!");
            Client.showInboxScene(userEmail);
        } else {
            statusLabel.setText("Errore invio email: indirizzo NON valido");
            statusLabel.setStyle("-fx-text-fill: red;");
            System.out.println("Errore invio email.");
        }
    }
}
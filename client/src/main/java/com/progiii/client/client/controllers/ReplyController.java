package com.progiii.client.client.controllers;

import com.progiii.client.client.Client;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * Controller per la scena di risposta
 * Gestisce la risposta, la risposta a tutti e l'inoltro delle email
 */
public class ReplyController {

    @FXML private TextField daField;
    @FXML private TextField toField;
    @FXML private TextField subjectField;
    @FXML private Label DataOraField;
    @FXML private TextArea messageBody;

    private String userEmail;
    private JSONObject selectedEmail;

    /**
     * Imposta l'email dell'utente.
     * @param email l'email dell'utente
     */
    public void setUserEmail(String email) {
        this.userEmail = email;
    }
    /**
     * Imposta i campi della risposta.
     * @param email l'email selezionata
     */
    public void setReplyFields(JSONObject email) {
        this.selectedEmail = email;
        daField.setText(email.optString("sender", "Sconosciuto")); // Mittente è l'utente attuale
        toField.setText(email.optString("receiver", "Sconosciuto")); // Destinatario predefinito
        subjectField.setText(email.optString("subject", "Nessun oggetto"));
        DataOraField.setText(email.optString("timestamp", "Data non disponibile"));
        messageBody.setText(email.optString("body", ""));
    }
    /**
     * Gestisce l'azione del pulsante "Indietro".
     * @throws IOException se si verifica un errore durante il caricamento della scena
     */
    @FXML
    private void HandleIndietro() throws IOException {
        Client.showInboxScene(userEmail);
    }
    /**
     * Gestisce l'azione del pulsante "Rispondi"
     * @throws IOException se si verifica un errore durante il caricamento della scena
     */
    @FXML
    private void HandleRispondi() throws IOException {
        openMessageScene(selectedEmail.optString("sender"), "Re: " + selectedEmail.optString("subject"));
    }
    /**
     * Gestisce l'azione del pulsante "Rispondi a tutti"
     * @throws IOException se si verifica un errore durante il caricamento della scena
     */
    @FXML
    private void HandleRispondiATutti() throws IOException {
        String sender = selectedEmail.optString("sender", "Sconosciuto");
        String receiver = selectedEmail.optString("receiver", "Sconosciuto");


        // Creiamo una lista e rimuoviamo il mittente se è uguale all'utente attuale
        List<String> recipients = new ArrayList<>();
        for (String rec : receiver.split(",")) {
            rec = rec.trim(); // Rimuove eventuali spazi extra
            if (!rec.isEmpty() && !rec.equals(userEmail)) {
                recipients.add(rec);
                System.out.println("Receiver aggiunto: " + rec);
            }
        }
        recipients.add(sender); // Aggiungiamo sempre il mittente originale

        // Uniamo tutti i destinatari separati da virgola
        String tutti = String.join(", ", recipients);
        openMessageScene(tutti, "Re: " + selectedEmail.optString("subject", "Senza oggetto"));
    }
    /**
     * Gestisce l'azione del pulsante "Inoltra"
     * @throws IOException se si verifica un errore durante il caricamento della scena
     */
    @FXML
    private void HandleInoltra() throws IOException {
        openMessageScene("", "Fwd: " + selectedEmail.optString("subject"));
    }
    /**
     * Apre la scena di messaggio con i dettagli dell'email
     * @param recipients i destinatari
     * @param subject l'oggetto
     * @throws IOException se si verifica un errore durante il caricamento della scena
     */
    private void openMessageScene(String recipients, String subject) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/progiii/client/client/message.fxml"));
        Parent root = loader.load();

        // Otteniamo il controller della scena di messaggio
        MessageController messageController = loader.getController();

        // Passiamo i dettagli dell'email alla nuova scena
        messageController.setFields(userEmail, recipients, subject, "\n---- Messaggio originale ----\n" + selectedEmail.optString("body"));

        // Cambio scena
        Stage stage = (Stage) daField.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}

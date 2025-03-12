package com.progiii.client.client;

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

public class ReplyController {

    @FXML private TextField daField;
    @FXML private TextField toField;
    @FXML private TextField subjectField;
    @FXML private Label DataOraField;
    @FXML private TextArea messageBody;

    private String userEmail;
    private JSONObject selectedEmail;

    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    public void setReplyFields(JSONObject email) {
        this.selectedEmail = email;
        daField.setText(email.optString("sender", "Sconosciuto")); // Mittente è l'utente attuale
        toField.setText(email.optString("receiver", "Sconosciuto")); // Destinatario predefinito
        subjectField.setText(email.optString("subject", "Nessun oggetto"));
        DataOraField.setText(email.optString("timestamp", "Data non disponibile"));
        messageBody.setText(email.optString("body", ""));
    }

    @FXML
    private void HandleIndietro() throws IOException {
        Client.showInboxScene(userEmail);
    }

    @FXML
    private void HandleRispondi() throws IOException {
        openMessageScene(selectedEmail.optString("sender"), "Re: " + selectedEmail.optString("subject"));
    }

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

    @FXML
    private void HandleInoltra() throws IOException {
        openMessageScene("", "Fwd: " + selectedEmail.optString("subject"));
    }

    private void openMessageScene(String recipients, String subject) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("message.fxml"));
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

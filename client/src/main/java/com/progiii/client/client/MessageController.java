package com.progiii.client.client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import org.json.JSONObject;

public class MessageController {
    @FXML private TextField toField;
    @FXML private TextField subjectField;
    @FXML private TextArea messageBody;
    @FXML private Label statusLabel;

    private String userEmail;

    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    public void setFields(String sender, String recipients, String subject, String message) {
        this.userEmail = sender;
        toField.setText(recipients);
        subjectField.setText(subject);
        messageBody.setText(message);
    }

    @FXML
    private void HandleIndietro() throws IOException {
        Client.showInboxScene(userEmail);
    }

    @FXML
    private void HandleInvia() {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

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

            out.println(email);

            System.out.println("Email inviata!");
            Client.showInboxScene(userEmail);
        } catch (IOException e) {
            statusLabel.setText("Errore invio email: server NON connesso (" + e.getMessage() + ")");
            statusLabel.setStyle("-fx-text-fill: red;");
            System.out.println("Errore invio email: " + e.getMessage());
        }
    }
}
package com.progiii.client.client;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;

import java.io.IOException;

public class ReplyController {

    @FXML private TextField daField;
    @FXML private TextField toField;
    @FXML private TextField subjectField;
    @FXML private TextArea messageBody;

    public void setupReply(String sender, String subject, String message) {
        daField.setText(sender);
        subjectField.setText("RE: " + subject);
        messageBody.setText("\n\n--- Messaggio originale ---\n" + message);
    }

    @FXML
    private void HandleIndietro() throws IOException {
        Client.showInboxScene("your-email@example.com");
    }
}
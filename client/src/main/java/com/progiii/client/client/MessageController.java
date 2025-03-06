package com.progiii.client.client;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import java.io.IOException;

public class MessageController {

    @FXML private TextField toField;
    @FXML private TextField subjectField;
    @FXML private TextArea messageBody;

    @FXML
    private void HandleIndietro() throws IOException {
        Client.showInboxScene("your-email@example.com"); // Placeholder email
    }

    @FXML
    private void HandleInvia() {
        System.out.println("Invio email a: " + toField.getText());
        System.out.println("Oggetto: " + subjectField.getText());
        System.out.println("Messaggio: " + messageBody.getText());
    }
}
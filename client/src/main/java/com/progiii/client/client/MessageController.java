package com.progiii.client.client;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import java.io.IOException;

public class MessageController {

    @FXML private TextField toField;
    @FXML private TextField subjectField;
    @FXML private TextArea messageBody;

    private String userEmail;  // Variabile per memorizzare l'email dell'utente

    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    @FXML
    private void HandleIndietro() throws IOException {
        Client.showInboxScene(userEmail); // Passa l'email corretta
    }

    @FXML
    private void HandleInvia() {
        System.out.println("Invio email a: " + toField.getText());
        System.out.println("Oggetto: " + subjectField.getText());
        System.out.println("Messaggio: " + messageBody.getText());
    }
}

package com.progiii.client.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.regex.Pattern;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private Label statusLabel;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();

        if (!isValidEmail(email)) {
            statusLabel.setText("Errore: Email non valida.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Simulazione di connessione al server (qui andrà la logica vera)
        boolean serverConnected = true;

        if (serverConnected) {
            changeSceneToInbox(email);
        } else {
            statusLabel.setText("Errore: Impossibile connettersi al server.");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private boolean isValidEmail(String email) {
        return Pattern.matches(EMAIL_REGEX, email);
    }

    private void changeSceneToInbox(String email) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("inbox.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            InboxController inboxController = fxmlLoader.getController();
            inboxController.setUserEmail(email);

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Inbox - " + email);
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Errore: Impossibile caricare la inbox.");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }
}
package com.progiii.client.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Pattern;

import org.json.JSONObject;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private Label statusLabel;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    @FXML
    public void handleLogin() {
        String email = emailField.getText().trim();

        if (!isValidEmail(email)) {
            statusLabel.setText("Errore: Email non valida.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Creiamo l'oggetto JSON con l'email
            JSONObject loginRequest = new JSONObject();
            loginRequest.put("type", "LOGIN");
            loginRequest.put("email", email);  // Aggiungiamo l'email

            // Invia l'oggetto JSON al server
            out.println(loginRequest);

            // Leggi la risposta dal server
            String response = in.readLine();
            if (response != null) {
                System.out.println(response);  // Stampa la risposta per il debug
                if (response.contains("SUCCESS")) {
                    changeSceneToInbox(email);
                } else {
                    statusLabel.setText("Errore: " + response);
                    statusLabel.setStyle("-fx-text-fill: red;");
                }
            }

        } catch (IOException e) {
            statusLabel.setText("Errore di connessione: " + e.getMessage());
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
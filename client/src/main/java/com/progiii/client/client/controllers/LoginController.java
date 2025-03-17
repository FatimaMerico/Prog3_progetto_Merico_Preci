package com.progiii.client.client.controllers;

import com.progiii.client.client.models.LoginModel;
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
/**
 * Controller per la scena di login
 * Gestisce l'autenticazione dell'utente e la connessione al server
 */

public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private Label statusLabel;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private LoginModel loginModel;

    public LoginController() {
        this.loginModel = new LoginModel();
    }
    /**
     * Gestisce l'azione del pulsante "Login"
     * @trows IOException se si verifica un errore durante la connessione al server
     */
    @FXML
    public void handleLogin() {
        String email = emailField.getText().trim();

        if (!isValidEmail(email)) {
            statusLabel.setText("Errore login: Email non valida");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (loginModel.validateLogin(email)) {
            changeSceneToInbox(email);
        }else {
            statusLabel.setText("Errore login: email non registrata o Server spento");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }
    /**
     * Verifica se l'email è valida
     * @param email l'email da verificare
     * @return true se l'email è valida, false altrimenti
     */
    private boolean isValidEmail(String email) {
        return Pattern.matches(EMAIL_REGEX, email);
    }
    /**
     * Cambia la scena alla inbox
     * @param email l'email dell'utente
     */
    private void changeSceneToInbox(String email) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/progiii/client/client/inbox.fxml"));
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
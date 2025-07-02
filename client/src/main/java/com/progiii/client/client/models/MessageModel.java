package com.progiii.client.client.models;

import javafx.scene.control.Label;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Gestisce la logica di invio delle email.
 */
public class MessageModel {
    /**
     * Invia un'email al server.
     *
     * @param emailData   i dati dell'email in formato JSON
     * @param statusLabel la label per mostrare lo stato dell'invio
     * @return true se l'invio ha successo, false altrimenti
     */
    public int sendEmail(JSONObject emailData, Label statusLabel) {
        try (Socket socket = new Socket("localhost", 3000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            // Invia l'email al server
            out.println(emailData);
            System.out.println("Email inviata al server");
            // Leggi la risposta dal server
            String response = in.readLine();
            if (response != null) {
                System.out.println(response);
                if (response.contains("SUCCESS")) {
                    return 1;
                } else if (response.contains("Email non valido")) {
                    return 3;
                } else {
                    return 2;
                }
            }
        } catch (IOException e) {
            System.out.println("Errore invio email: " + e.getMessage());
            statusLabel.setText("Errore invio email: server NON connesso (" + e.getMessage() + ")");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
        return 0;
    }
}
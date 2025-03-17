package com.progiii.client.client.models;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Gestisce la logica di autenticazione dell'utente.
 */
public class LoginModel{
    /**
     * Verifica se l'email è valida e registrata.
     * @param email l'email da verificare
     * @return true se l'email è valida e registrata, false altrimenti
     */
    public boolean validateLogin(String email) {
        try (Socket socket = new Socket("localhost", 3000);
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
                System.out.println(response);
                if (response.contains("SUCCESS")) {
                    return true;
                }
            }else {
                System.out.println("Errore durante la lettura della risposta dal server nel login");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
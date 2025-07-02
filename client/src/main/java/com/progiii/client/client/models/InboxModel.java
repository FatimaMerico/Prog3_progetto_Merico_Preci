package com.progiii.client.client.models;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Classe che si occupa di gestire la comunicazione con il server per la gestione della casella di posta
 */
public class InboxModel {

    //public InboxModel() {}
    /**
     * Recupera le email di un utente
     * @param userMail email dell'utente
     * @return lista di email
     */
    public String fetchEmails(String userMail) {
        try (Socket socket = new Socket("localhost", 3000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner in = new Scanner(socket.getInputStream())) {

            // Creazione della richiesta PING sotto forma di JSON
            JSONObject pingRequest = new JSONObject();
            pingRequest.put("type", "PING");
            pingRequest.put("sender", userMail);
            // Invio della richiesta PING al server
            out.println(pingRequest);
            // Lettura della risposta dal server (Lista di email)

            if (in.hasNext()) {
                return in.nextLine();
            }
        } catch (IOException e) {
            System.out.println("Errore ricezione email: " + e.getMessage());
        }
        return null ;
    }

    /**
     * Elimina le email di un utente
     * @param userMail email dell'utente
     * @param emailId id dell'email da eliminare
     * @return true se l'eliminazione è avvenuta con successo, false altrimenti
     */
    public boolean deleteEmails(String userMail, int emailId) {
        try (Socket socket = new Socket("localhost", 3000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Creiamo l'oggetto JSON per la richiesta
            JSONObject deleteRequest = new JSONObject();
            deleteRequest.put("type", "ELIMINA");
            deleteRequest.put("richiedente", userMail);
            deleteRequest.put("email_id", emailId);

            // Invia il JSON al server
            out.println(deleteRequest);
            System.out.println("Richiesta di eliminazione inviata: " + deleteRequest);

            // Leggi la risposta del server
            String response = in.readLine();
            if (response != null && response.contains("SUCCESS")) {
                System.out.println("Risposta del server: " + response);
                System.out.println("Email eliminata con successo.");
                return true;
                }
            } catch (IOException e) {
                System.out.println("Errore nella comunicazione con il server: " + e.getMessage());
                e.printStackTrace();
            }
        System.out.println("Errore nell'eliminazione dell'email.");
        return false;
    }
}
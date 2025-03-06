package com.progiii.demo.demo;

import org.json.JSONObject;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ServerController controller;
    private BufferedReader in;
    private PrintWriter out;
    private static final String USERS_FILE = "users.txt";

    public ClientHandler(Socket socket, ServerController controller) {
        this.socket = socket;
        this.controller = controller;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("Benvenuto nel Mail Server!");

            // Autenticazione
            String loginEmail = in.readLine();
            if (!isUserRegistered(loginEmail)) {
                sendError("Utente non registrato");
                controller.logMessage("Tentativo di accesso fallito per: " + loginEmail);
                return;
            }
            sendSuccess("Accesso effettuato con successo per: " + loginEmail);

            // Ricezione email
            String emailJson;
            while ((emailJson = in.readLine()) != null) {
                JSONObject emailObject = new JSONObject(emailJson);
                String sender = emailObject.getString("sender");
                String recipient = emailObject.getString("receiver");
                String subject = emailObject.getString("subject");
                String body = emailObject.getString("body");

                if (!isUserRegistered(recipient)) {
                    sendError("Destinatario non registrato");
                    controller.logMessage("Email non inviata, destinatario non trovato: " + recipient);
                    continue;
                }

                String id = (sender + recipient + subject + body + java.time.LocalDateTime.now()).hashCode() + "";
                JSONObject newEmail = new JSONObject();
                newEmail.put("id", id);
                newEmail.put("sender", sender);
                newEmail.put("receiver", recipient);
                newEmail.put("subject", subject);
                newEmail.put("body", body);
                newEmail.put("timestamp", java.time.LocalDateTime.now().toString());

                MailBox.saveEmail(newEmail.toString());
                sendSuccess("Email inviata!");
                controller.logMessage("Email inviata da " + sender + " a " + recipient);
            }
        } catch (IOException e) {
            controller.logMessage("Errore con client: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                controller.logMessage("Errore chiusura socket: " + e.getMessage());
            }
        }
    }

    private boolean isUserRegistered(String email) throws IOException {
        List<String> users = Files.readAllLines(Paths.get(USERS_FILE));
        return users.contains(email);
    }

    private void sendError(String message) {
        JSONObject response = new JSONObject();
        response.put("status", "ERROR");
        response.put("message", message);
        out.println(response.toString());
    }

    private void sendSuccess(String message) {
        JSONObject response = new JSONObject();
        response.put("status", "SUCCESS");
        response.put("message", message);
        out.println(response.toString());
    }
}

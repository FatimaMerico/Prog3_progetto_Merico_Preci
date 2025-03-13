package com.progiii.demo.demo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ClientHandler implements Runnable {
    private Socket socket;
    private static ServerController controller;
    private BufferedReader in;
    private PrintWriter out;
    private String userEmail;
    private static final String USERS_FILE = "server/src/main/resources/com/progiii/demo/demo/users.txt";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private final Map<String, Object> fileLocks; //Mappa di lock per ogni file .json

    public ClientHandler(Socket socket, ServerController controller, Map<String, Object> fileLocks) {
        this.socket = socket;
        this.controller = controller;
        this.fileLocks = fileLocks;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String request;
            while ((request = in.readLine()) != null) {
                try {
                    JSONObject jsonRequest = new JSONObject(request);
                    String type = jsonRequest.getString("type");
                    switch (type) {
                        case "LOGIN":
                            handleLogin(jsonRequest);
                            break;
                        case "PING":
                            handlePing(jsonRequest);
                            break;
                        case "EMAIL":
                            handleEmail(jsonRequest);
                            break;
                        case "ELIMINA":
                            handleDeleteRequest(jsonRequest);
                            break;
                        default:
                            sendError("Richiesta non riconosciuta");
                            break;
                    }
                } catch (JSONException e) {
                    controller.logMessage("Errore nel parsing JSON: " + e.getMessage());
                    sendError("Errore nel parsing della richiesta");
                }
            }
        } catch (IOException e) {
            controller.logMessage("Errore client: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                controller.logMessage("Errore chiusura socket: " + e.getMessage());
            }
        }
    }

    private void handleLogin(JSONObject jsonRequest) {
        String loginEmail = jsonRequest.getString("email");
        controller.logMessage("Tentativo di login con email: " + loginEmail);
        if (!isUserRegistered(loginEmail)) {
            sendError("Utente non registrato");
        } else {
            userEmail = loginEmail;
            controller.logMessage("Accesso effettuato con successo per: " + loginEmail);
            sendSuccess("Accesso effettuato con successo per: " + loginEmail);
            markAllEmailsAsUnread(loginEmail);
        }
    }

    private void handlePing(JSONObject request) {
        try {
            String userEmail = request.getString("sender");
            if (!isValidEmail(userEmail)) {
                sendError("Email non valido");
                return;
            }
            String filename = userEmail.substring(0, userEmail.indexOf('@')) + ".json";
            Path filePath = Paths.get("server/src/main/resources/com/progiii/demo/demo/" + filename);

            // Sincronizza l'accesso al file .json
            synchronized (fileLocks.get(filename)) {
                if (!Files.exists(filePath)) {
                    out.println("[]");
                    return;
                }

                List<String> lines = Files.readAllLines(filePath);
                String content = String.join("", lines).trim();
                if (content.isEmpty()) {
                    out.println("[]");
                    return;
                }

                JSONArray emailArray = new JSONArray(content);
                JSONArray unreadEmails = new JSONArray();

                for (int i = 0; i < emailArray.length(); i++) {
                    JSONObject email = emailArray.getJSONObject(i);
                    if ("DA LEGGERE".equals(email.getString("status"))) {
                        unreadEmails.put(email);
                        email.put("status", "LETTO");
                    }
                }

                out.println(unreadEmails);
                out.flush();

                Files.write(filePath, emailArray.toString(4).getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (Exception e) {
            System.out.println("Errore nella gestione del PING: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleEmail(JSONObject emailData) {
        try {
            System.out.println("Salvataggio email in corso...");
            System.out.println("Dati ricevuti: " + emailData.toString());

            int emailId = emailData.toString().hashCode();
            emailData.put("id", emailId);

            String receivers = emailData.getString("receiver").toLowerCase();
            String[] receiverList = receivers.split("\\s*,\\s*");

            for (String receiver : receiverList) {
                if (!receiver.contains("@")) {
                    System.out.println("Errore: indirizzo email non valido - " + receiver);
                    continue;
                }

                String filename = receiver.substring(0, receiver.indexOf('@')) + ".json";
                Path filePath = Paths.get("server/src/main/resources/com/progiii/demo/demo/" + filename);

                // Sincronizza l'accesso al file .json
                synchronized (fileLocks.get(filename)) {
                    JSONArray emailArray;
                    if (Files.exists(filePath)) {
                        List<String> lines = Files.readAllLines(filePath);
                        String content = String.join("", lines).trim();
                        emailArray = content.isEmpty() ? new JSONArray() : new JSONArray(content);
                    } else {
                        emailArray = new JSONArray();
                    }

                    emailArray.put(emailData);
                    Files.write(filePath, emailArray.toString(4).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                    System.out.println("Email salvata con successo per " + receiver + " in " + filename);
                }
            }
        } catch (Exception e) {
            System.out.println("Errore nel salvataggio email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDeleteRequest(JSONObject requestJson) {
        try {
            if (!requestJson.getString("type").equals("ELIMINA")) {
                controller.logMessage("Richiesta non valida.");
                sendError("Tipo di richiesta non valido.");
                return;
            }

            String richiedente = requestJson.getString("richiedente");
            int emailId = requestJson.getInt("email_id");

            String filename = richiedente.substring(0, richiedente.indexOf('@')) + ".json";
            Path filePath = Paths.get("server/src/main/resources/com/progiii/demo/demo/" + filename);

            // Sincronizza l'accesso al file .json
            synchronized (fileLocks.get(filename)) {
                if (!Files.exists(filePath)) {
                    controller.logMessage("Errore: File utente non trovato.");
                    sendError("File utente non trovato.");
                    return;
                }

                String content = Files.readString(filePath);
                JSONArray emailArray = new JSONArray(content);

                boolean emailTrovata = false;
                for (int i = 0; i < emailArray.length(); i++) {
                    JSONObject email = emailArray.getJSONObject(i);
                    if (email.getInt("id") == emailId) {
                        emailArray.remove(i);
                        emailTrovata = true;
                        break;
                    }
                }

                if (emailTrovata) {
                    Files.write(filePath, emailArray.toString(4).getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
                    controller.logMessage("Email eliminata con successo per " + richiedente);
                    sendSuccess("Email eliminata con successo.");
                } else {
                    controller.logMessage("Email non trovata nel file di " + richiedente);
                    sendError("Email non trovata.");
                }
            }
        } catch (Exception e) {
            controller.logMessage("Errore durante l'eliminazione dell'email: " + e.getMessage());
            sendError("Errore durante l'eliminazione dell'email.");
            e.printStackTrace();
        }
    }

    private void markAllEmailsAsUnread(String userEmail) {
        try {
            String filename = userEmail.substring(0, userEmail.indexOf('@')) + ".json";
            Path filePath = Paths.get("server/src/main/resources/com/progiii/demo/demo/" + filename);

            // Sincronizza l'accesso al file .json
            synchronized (fileLocks.get(filename)) {
                if (!Files.exists(filePath)) {
                    return;
                }

                List<String> lines = Files.readAllLines(filePath);
                String content = String.join("", lines).trim();
                if (content.isEmpty()) {
                    return;
                }

                JSONArray emailArray = new JSONArray(content);
                for (int i = 0; i < emailArray.length(); i++) {
                    JSONObject email = emailArray.getJSONObject(i);
                    email.put("status", "DA LEGGERE");
                }

                Files.write(filePath, emailArray.toString(4).getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
                System.out.println("LOGIN: Tutte le email di " + userEmail + " sono state impostate su 'DA LEGGERE'.");
            }
        } catch (Exception e) {
            System.out.println("Errore nell'aggiornamento delle email a 'DA LEGGERE' per " + userEmail + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendError(String message) {
        JSONObject response = new JSONObject();
        response.put("status", "ERROR");
        response.put("message", message);
        out.println(response);
    }

    private void sendSuccess(String message) {
        JSONObject response = new JSONObject();
        response.put("status", "SUCCESS");
        response.put("message", message);
        out.println(response);
    }

    private boolean isValidEmail(String email) {
        return Pattern.matches(EMAIL_REGEX, email);
    }

    private static boolean isUserRegistered(String email) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equalsIgnoreCase(email)) {
                    return true;
                }
            }
        } catch (IOException e) {
            controller.logMessage("Errore lettura utenti: " + e.getMessage());
        }
        return false;
    }
}
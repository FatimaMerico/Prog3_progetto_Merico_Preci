package com.progiii.demo.demo;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;

public class MailBox {
    private static final String FILE_PATH = "emails.json";

    /*public static void saveEmail(String emailJson) {
        JSONArray emails = loadEmails();
        emails.put(new JSONObject(emailJson));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write(emails.toString(4)); // Formattazione con indentazione
        } catch (IOException e) {
            System.out.println("Errore salvataggio email: " + e.getMessage());
        }
    }

    public static JSONArray loadEmails() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            return new JSONArray(content.toString());
        } catch (IOException e) {
            return new JSONArray(); // Restituisce un array vuoto in caso di errore
        }
    }*/
}

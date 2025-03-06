package com.progiii.demo.demo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MailBox {
    private static final String FILE_PATH = "emails.txt";

    public static void saveEmail(String email) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(email);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Errore salvataggio email: " + e.getMessage());
        }
    }

    public static List<String> loadEmails() {
        List<String> emails = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                emails.add(line);
            }
        } catch (IOException e) {
            System.out.println("Errore lettura email: " + e.getMessage());
        }
        return emails;
    }
}

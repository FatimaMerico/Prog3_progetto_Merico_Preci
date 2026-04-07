package com.progiii.demo.demo.network;

import com.progiii.demo.demo.controllers.ServerController;
import com.progiii.demo.demo.models.ClientHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
/**
 * Gestisce il server di posta elettronica
 * Implementa l'interfaccia Runnable per gestire il server in un thread separato
 */
public class MailServer implements Runnable {
    private static final int PORT = 3000;
    private ServerController controller;
    private ServerSocket serverSocket;
    private boolean running = true;
    private static final Map<String, Object> fileLocks = new HashMap<>();//Mappa di lock per ogni file .json

    /**
     * Costruttore della classe MailServer.
     * @param controller il controller del server
     */
    public MailServer(ServerController controller) {
        this.controller = controller;
        initializeFileLocks(); //Inizializza i lock all'avvio del server
    }

    /**
     * Inizializza i lock per ogni file .json
     */
    private void initializeFileLocks() {
        Path usersFile = Paths.get("/server/src/main/resources/com/progiii/demo/demo/users.txt");

        try (BufferedReader reader = Files.newBufferedReader(usersFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String email = line.trim();
                System.out.println("inizializzazione lock");
                System.out.println(email);
                if (email.contains("@")) {
                    String username = email.substring(0, email.indexOf("@"));
                    String jsonFileName = username + ".json";
                    fileLocks.put(jsonFileName, new Object());
                }
            }
        } catch (IOException e) {
            controller.logMessage("Errore inizializzazione fileLocks: " + e.getMessage());
        }
    }

    /**
     * Metodo principale che avvia il server (parte logica)
     */
    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PORT);
            controller.logMessage("Server in ascolto sulla porta " + PORT);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, controller, fileLocks);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            controller.logMessage(e.getMessage());
        }
    }
    /**
     * Arresta il server (parte logica)
     */
    public void stop() {
        running = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            controller.logMessage("Errore chiusura server: " + e.getMessage());
        }
    }
}
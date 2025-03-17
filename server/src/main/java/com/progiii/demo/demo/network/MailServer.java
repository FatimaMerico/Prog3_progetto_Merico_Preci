package com.progiii.demo.demo.network;

import com.progiii.demo.demo.controllers.ServerController;
import com.progiii.demo.demo.models.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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
        fileLocks.put("fatima.json", new Object());
        fileLocks.put("monika.json", new Object());
        fileLocks.put("persona.json", new Object());
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
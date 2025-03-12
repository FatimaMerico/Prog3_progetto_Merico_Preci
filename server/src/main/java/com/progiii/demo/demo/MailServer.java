package com.progiii.demo.demo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MailServer implements Runnable {
    private static final int PORT = 12345;
    private ServerController controller;
    private ServerSocket serverSocket;
    private boolean running = true;

    public MailServer(ServerController controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PORT);
            controller.logMessage("Server in ascolto sulla porta " + PORT);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, controller);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            controller.logMessage(e.getMessage());
        }
    }

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

package com.progiii.demo.demo;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ServerController controller;
    private BufferedReader in;
    private PrintWriter out;

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
            String email;
            while ((email = in.readLine()) != null) {
                controller.logMessage("Messaggio ricevuto: " + email);
                MailBox.saveEmail(email);
                out.println("Email ricevuta!");
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
}

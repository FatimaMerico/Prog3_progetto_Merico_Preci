package com.progiii.client.client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class InboxController {

    @FXML private Label userMail;

    public void setUserEmail(String email) {
        userMail.setText(email);
    }

    @FXML
    private void HandleScrivi() throws IOException {
        Client.showMessageScene();
    }

    @FXML
    private void HandleLogout() throws IOException {
        Client.showLoginScene();
    }

    @FXML
    private void HandleElimina() throws IOException {
        System.out.println("Eliminazione email");
    }
}
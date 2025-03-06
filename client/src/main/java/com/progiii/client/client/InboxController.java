package com.progiii.client.client;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class InboxController {
    @FXML private TextField toField;
    @FXML private TextField subjectField;
    @FXML private TextArea messageBody;

    @FXML
    private void onSendButtonClick() {
        System.out.println("Message Sent to: " + toField.getText());
    }

    @FXML
    private void HandleScrivi() {
        //cambiare scena
        // vado in message.fxml
    }

    @FXML
    private void HandleElimina() {
        //richiesta al server per eliminare quella mail di quell'utente
        //la tolgo dal json
    }

    @FXML
    private void HandleLogout() {
        // cambio scena
        // vado in login.fxml
    }

    @FXML
    private void HandleMessaggio() {
       //dom dice di gestirlo direttamente con java, senza mettere on Action in fxml

    }

}

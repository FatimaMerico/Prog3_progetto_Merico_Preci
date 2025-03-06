package com.progiii.client.client;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class MessageController {
    @FXML
    private TextField toField;
    @FXML private TextField subjectField;
    @FXML private TextArea messageBody;


    @FXML
    //ritorno all'inbox
    private void HandleIndietro() {
        System.out.println("Message Sent to: " + toField.getText());
        //cambia scene
    }
    @FXML

    private void HandleInvia() {
        //parsifica la mail
        //salva l'oggetto e il messaggio

    }
}

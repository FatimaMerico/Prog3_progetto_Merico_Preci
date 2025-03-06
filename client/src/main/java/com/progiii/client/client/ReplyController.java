package com.progiii.client.client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ReplyController {
    @FXML
    private TextField daField;
    @FXML private TextField toField;
    @FXML private TextField subjectField;
    @FXML private Label DataOraField;
    @FXML private TextArea messageBody;

    @FXML
    private void HandleIndietro() {
        //ritorna alla inbox
    }

    @FXML
    private void HandleRispondi() {
        //parsificare l'emittente
        //cambiare scena a message.fxml
        //inserendo nel destinatatio l'emittente
    }

    @FXML
    private void HandleRispondiATutti() {
        //parsificare l'emittente e gli eentuali cc
        //cambiare scena a message.fxml
        //inserendo nel destinatatio l'emittente e cc
    }

    @FXML
    private void HandleInoltra() {
        //ricopiare il messaggio e l'oggetto
        //cambiando scene in message
    }


}

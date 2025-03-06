package com.progiii.client.client;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.JSONObject;

import java.io.IOException;

public class InboxController {
    @FXML private Label userMail; // Mostra l'email dell'utente
    @FXML private TableView<JSONObject> emailTable;
    @FXML private TableColumn<JSONObject, String> receiversColumn;
    @FXML private TableColumn<JSONObject, String> subjectColumn;
    @FXML private TableColumn<JSONObject, String> previewColumn;

    private ObservableList<JSONObject> emailList;

    public InboxController() {
        emailList = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        // Collega le colonne ai dati JSON
        receiversColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getString("receiver")));
        subjectColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getString("subject")));
        previewColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getString("body")));

        // Associa l'ObservableList al TableView
        emailTable.setItems(emailList);
    }

    public void setUserEmail(String email) {
        userMail.setText(email);
    }

    public void loadEmails(ObservableList<String> emailData) {
        emailList.clear(); // Pulisce la lista esistente
        for (String emailJson : emailData) {
            emailList.add(new JSONObject(emailJson)); // Aggiunge ogni email come JSONObject
        }
    }

    public void addEmail(String emailJson) {
        emailList.add(new JSONObject(emailJson)); // Aggiunge una nuova email dinamicamente
    }

    // Funzioni per i bottoni (rimangono invariate)
    @FXML
    private void HandleScrivi() throws IOException {
        Client.showMessageScene(userMail.getText()); // Passa l'email corrente
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

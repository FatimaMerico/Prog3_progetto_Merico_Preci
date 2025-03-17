module com.progiii.client.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.json;
    requires java.desktop;


    opens com.progiii.client.client to javafx.fxml;
    exports com.progiii.client.client;
    exports com.progiii.client.client.controllers;
    opens com.progiii.client.client.controllers to javafx.fxml;
}
module com.progiii.client.client {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.progiii.client.client to javafx.fxml;
    exports com.progiii.client.client;
}
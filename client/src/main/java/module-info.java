module com.progiii.client.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.json;


    opens com.progiii.client.client to javafx.fxml;
    exports com.progiii.client.client;
}
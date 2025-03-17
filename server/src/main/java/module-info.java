module com.progiii.demo.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;


    opens com.progiii.demo.demo to javafx.fxml;
    exports com.progiii.demo.demo;
    exports com.progiii.demo.demo.controllers;
    opens com.progiii.demo.demo.controllers to javafx.fxml;
    exports com.progiii.demo.demo.network;
    opens com.progiii.demo.demo.network to javafx.fxml;
    exports com.progiii.demo.demo.models;
    opens com.progiii.demo.demo.models to javafx.fxml;
}
module com.progiii.demo.demo {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.progiii.demo.demo to javafx.fxml;
    exports com.progiii.demo.demo;
}
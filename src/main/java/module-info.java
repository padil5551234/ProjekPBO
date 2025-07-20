module com.example.uji_coba {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.zaxxer.hikari;
    requires org.controlsfx.controls;
    requires org.slf4j.simple;
    requires java.prefs;


    opens com.example.uji_coba to javafx.fxml;
    exports com.example.uji_coba;
}
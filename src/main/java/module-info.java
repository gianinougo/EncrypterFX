module com.example.encrypterfx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.encrypterfx to javafx.fxml;
    exports com.example.encrypterfx;
}
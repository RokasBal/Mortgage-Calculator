module com.example.mortgagecalculator {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens com.example.mortgagecalculator to javafx.fxml;
    opens Utility to javafx.base;
    exports com.example.mortgagecalculator;
}
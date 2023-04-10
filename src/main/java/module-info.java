module com.example.filegen {
    requires javafx.controls;
    requires javafx.fxml;
    requires ini4j;


    opens pl.cheily.filegen to javafx.fxml;
    exports pl.cheily.filegen;
    exports pl.cheily.filegen.Utils;
    opens pl.cheily.filegen.Utils to javafx.fxml;
}
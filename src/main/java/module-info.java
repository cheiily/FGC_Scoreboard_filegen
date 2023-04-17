module pl.cheily.filegen {
    requires javafx.controls;
    requires javafx.fxml;
    requires ini4j;
    requires com.opencsv;
    requires jdk.httpserver;
    requires org.json;


    opens pl.cheily.filegen to javafx.fxml;
    exports pl.cheily.filegen;
    exports pl.cheily.filegen.Utils;
    opens pl.cheily.filegen.Utils to javafx.fxml;
    exports pl.cheily.filegen.UI;
    opens pl.cheily.filegen.UI to javafx.fxml;
    exports pl.cheily.filegen.LocalData;
    opens pl.cheily.filegen.LocalData to javafx.fxml;
}
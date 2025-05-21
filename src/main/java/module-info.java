module pl.cheily.filegen {
    requires javafx.controls;
    requires javafx.fxml;
    requires ini4j;
    requires com.opencsv;
    requires jdk.httpserver;
    requires org.json;
    requires Java.WebSocket;
    requires java.desktop;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;
    requires org.controlsfx.controls;

    opens pl.cheily.filegen to javafx.fxml;
    exports pl.cheily.filegen;
    exports pl.cheily.filegen.Utils;
    opens pl.cheily.filegen.Utils to javafx.fxml;
    exports pl.cheily.filegen.UI;
    opens pl.cheily.filegen.UI to javafx.fxml;
    exports pl.cheily.filegen.LocalData;
    opens pl.cheily.filegen.LocalData to javafx.fxml;
    exports pl.cheily.filegen.LocalData.FileManagement.Meta;
    opens pl.cheily.filegen.LocalData.FileManagement.Meta to javafx.fxml;
    exports pl.cheily.filegen.LocalData.FileManagement.Output.Formatting;
    opens pl.cheily.filegen.LocalData.FileManagement.Output.Formatting to javafx.fxml;
    exports pl.cheily.filegen.LocalData.FileManagement.Output.Writing;
    opens pl.cheily.filegen.LocalData.FileManagement.Output.Writing to javafx.fxml;
    exports pl.cheily.filegen.LocalData.FileManagement.Meta.Match;
}
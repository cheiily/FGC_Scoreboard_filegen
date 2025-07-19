package pl.cheily.filegen;

import ch.qos.logback.classic.spi.ILoggingEvent;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import pl.cheily.filegen.Utils.Pair;

import java.lang.reflect.Modifier;
import java.util.*;

public class AlertingAppender extends ch.qos.logback.core.AppenderBase<ILoggingEvent> {
    private static Map<String, ButtonType> buttonMappings;
    private static ButtonType getButtonType(String type) {
        return buttonMappings.get(type);
    }
    {
        var list = Arrays.stream(ButtonType.class.getDeclaredFields())
                .filter(f -> Modifier.isStatic(f.getModifiers()))
                .map(f -> {
                    try {
                        return new Pair<String, ButtonType>(f.getName(), (ButtonType)f.get(null));
                    } catch (IllegalAccessException e) {
                        LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME).error("Failed to initialize Alerting Appender button type mappings for {}.", f.getName());
                    }
                    return null;
                }).filter(Objects::nonNull)
                .toList();

        buttonMappings = new HashMap<>();
        for (Pair<String, ButtonType> pair : list) {
            buttonMappings.put(pair.left(), pair.right());
        }
    }

    public AlertingAppender() {
        super();
        setName("AlertingAppender");
        start();
    }

    //0 - "ALERT"
    //1 - AlertType
    //2+ - ButtonType
    @Override
    protected void append(ILoggingEvent iLoggingEvent) {
        List<Marker> markers = iLoggingEvent.getMarkerList();
        if (markers.isEmpty()) return;
        if (!markers.get(0).equals(MarkerFactory.getMarker("ALERT"))) return;
        if (markers.size() == 1) {
            showAlert(Alert.AlertType.ERROR, iLoggingEvent.getMessage());
            return;
        }

        List<ButtonType> buttons = new ArrayList<>();
        for (int i = 2; i < markers.size(); ++i) {
            ButtonType button = buttonMappings.get(markers.get(i).getName());
            if (button == null) continue;
            buttons.add(button);
        }
        showAlert(Alert.AlertType.valueOf(markers.get(1).getName()), iLoggingEvent.getMessage(), buttons.toArray(new ButtonType[0]));
    }

    private void showAlert(Alert.AlertType type, String message, ButtonType... buttons) {
        if (Platform.isFxApplicationThread()) {
            doShowAlert(type, message, buttons);
        } else {
            Platform.runLater(() -> doShowAlert(type, message, buttons));
        }
    }

    private void doShowAlert(Alert.AlertType type, String message, ButtonType... buttons) {
        Alert alert = new Alert(type, message, buttons);
        alert.showAndWait();
    }
}

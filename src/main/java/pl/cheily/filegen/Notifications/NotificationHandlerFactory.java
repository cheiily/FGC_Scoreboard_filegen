package pl.cheily.filegen.Notifications;

import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.action.Action;
import org.slf4j.LoggerFactory;
import pl.cheily.filegen.ScoreboardApplication;

import java.util.function.Consumer;

public class NotificationHandlerFactory {
    public static Consumer<NotificationData> getHandler(int version) {
        return switch (version) {
            case 1 -> version1Handler();
            default -> versionUndefinedHandler();
        };
    }

    public static Consumer<NotificationData> version1Handler() {
        return notification -> {
            NotificationData.ButtonData button;
            var display = Notifications.create()
                    .title(notification.header)
                    .text(notification.content)
                    .hideAfter(Duration.seconds(10))
                    .onAction(event -> System.out.println("here"));
            if ((button = notification.button) != null) {
                display.action(new Action(
                        button.text,
                        event -> new Thread(() -> {
                            ScoreboardApplication.instance.getHostServices().showDocument(button.url);
                            if (notification.repeat) {
                                RepeatingNotificationMemory.setInteracted(notification);
                            }
                        }).run()
                ));
            }

            // Because there is no onAction called upon clicking the close button, nor a way to subscribe or even access it from outside,
            // we simply remove that possibility.
            display.hideCloseButton();

            if (notification.repeat) {
                display.onAction( evt -> {
                    RepeatingNotificationMemory.setInteracted(notification);
                });

                RepeatingNotificationMemory.incrementShowCount(notification);
            }

            display.show();
        };
    }

    public static Consumer<NotificationData> versionUndefinedHandler() {
        return notification -> {
            LoggerFactory.getLogger(NotificationAPIChecker.class).warn(
                    "Received notification with undefined or unhandled version: {}", notification
            );
        };
    }
}

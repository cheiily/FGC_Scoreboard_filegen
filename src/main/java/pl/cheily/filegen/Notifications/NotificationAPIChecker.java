package pl.cheily.filegen.Notifications;

import javafx.application.Platform;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class NotificationAPIChecker {
    private final static Logger logger = LoggerFactory.getLogger(NotificationAPIChecker.class);
    private static final String API_URL = "Https://cheily.one/api/sscnotif.json";
    private static final HashMap<Integer, Consumer<NotificationData>> handlerMap = new HashMap<>(2);
    private static SharedNotificationCache cache = SharedNotificationCache.empty();

    public static int currentNotifs = 0;
    public static final int maxNotifs = 3;

    public static void queueNotificationChecks() {
        cache = SharedNotificationCache.load();

        Platform.runLater(() -> {
            JSONObject response = getResponse();
            if (response == null) {
                logger.error("Failed to fetch notifications from API, response object null.");
                return;
            }

            if (response.getInt("amt") <= 0) {
                logger.info("No notifications to process.");
                return;
            }
            logger.info("Processing {} notifications.", response.getInt("amt"));

            Stream<NotificationData> notifications = response.getJSONArray("data").toList().stream()
                    .map(NotificationData::parse);
            if (!response.getBoolean("filter")) {
                logger.debug("Notifications are not pre-filtered. Filtering them now.");
                notifications = notifications.filter(NotificationAPIChecker::filterNotif);
            }
            notifications.forEach(notif -> {
                if (currentNotifs >= maxNotifs) {
                    logger.info("Maximum number of notifications reached.");
                    return;
                }
                if (notif.id <= cache.last_id)
                    return;
                // todo "repeat" caching

                getHandler(notif.version).accept(notif);
                currentNotifs++;
                cache = SharedNotificationCache.handledNow(notif);
                new Thread(() -> {
                    try {
                        Thread.sleep(10 * 1000);
                    } catch (InterruptedException e) {
                        logger.error("Notification count reduce thread interrupted. Reducing anyway", e);
                    } finally {
                        currentNotifs--;
                    }
                }).start();
            });
        });
    }

    private static Consumer<NotificationData> getHandler(int version) {
        if (!handlerMap.containsKey(version))
            handlerMap.put(version, NotificationHandlerFactory.getHandler(version));
        return handlerMap.get(version);
    }

    private static boolean filterNotif(NotificationData notification) {
        if (notification == null) {
            return false;
        }

        if (notification.draft)
            return false;

        try {
            if (notification.validDays > -1) {
                return ZonedDateTime.now().compareTo(notification.postTime.plusDays(notification.validDays)) < 1;
            }
        } catch (DateTimeParseException e) {
            logger.error("Could not filter notification due to a date parsing error. Discarding. ", e);
            return false;
        }

        return true;
    }

    private static JSONObject getResponse() {
        try {
            HttpsURLConnection connection = getConnection();

            InputStream inputStream = connection.getInputStream();
            StringBuilder responseBuilder = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }

                return new JSONObject(responseBuilder.toString());
            } catch (IOException e) {
                logger.error("Error reading response from API", e);
                return null;
            }
        } catch (IOException e) {
            logger.error("Error connecting to cheily.one API.", e);
            return null;
        }
    }

    private static HttpsURLConnection getConnection() throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) (new URL(API_URL).openConnection());
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");
        return connection;
    }
}

package pl.cheily.filegen.Notifications;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class NotificationData {
    public int version;
    public boolean draft;
    public ZonedDateTime postTime;
    public int validDays;
    public String header;
    public String content;
    public ButtonData button;

    public static class ButtonData {
        public String text;
        public String url;

        public ButtonData(String text, String url) {
            this.text = text;
            this.url = url;
        }
    }

    public NotificationData(int version, boolean draft, ZonedDateTime postTime, int validDays, String header, String content, ButtonData button) {
        this.version = version;
        this.draft = draft;
        this.postTime = postTime;
        this.validDays = validDays;
        this.header = header;
        this.content = content;
        this.button = button;
    }

    public static NotificationData parse(Object obj) {
        HashMap jsonMap = (HashMap) obj;
        return new NotificationData(
                (int)jsonMap.get("version"),
                (boolean)jsonMap.get("draft"),
                ZonedDateTime.parse((String)jsonMap.get("posttime"), DateTimeFormatter.ISO_DATE_TIME),
                (int)jsonMap.get("validdays"),
                (String)jsonMap.get("header"),
                (String)jsonMap.get("content"),
                jsonMap.get("button") == null ? null : new ButtonData(
                        (String)((HashMap)jsonMap.get("button")).get("text"),
                        (String)((HashMap)jsonMap.get("button")).get("url")
                )
        );
    }
}

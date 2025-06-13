package pl.cheily.filegen.Notifications;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class NotificationData {
    public int version;
    public int id;
    public boolean draft;
    public boolean repeat;
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

    public NotificationData(int version, int id, boolean draft, boolean repeat, ZonedDateTime postTime, int validDays, String header, String content, ButtonData button) {
        this.version = version;
        this.id = id;
        this.draft = draft;
        this.repeat = repeat;
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
                (int)jsonMap.get("id"),
                (boolean)jsonMap.get("draft"),
                (boolean)jsonMap.get("repeat"),
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

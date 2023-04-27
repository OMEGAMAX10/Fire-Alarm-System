package com.mta.firealarm;

public class Alert {
    private String eventType;
    private String source;
    private String timestamp;
    private String imageUrl;

    public Alert(String eventType, String source, String timestamp, String imageUrl) {
        this.eventType = eventType;
        this.source = source;
        this.timestamp = timestamp;
        this.imageUrl = imageUrl;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

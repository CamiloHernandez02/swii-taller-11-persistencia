package com.rica.persistence.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

@Document(collection = "order_event_logs")
public class OrderEventLog implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private String orderId;
    private String eventId;
    private String eventType;
    private String eventData;
    private LocalDateTime occurredAt;
    private LocalDateTime loggedAt;

    public OrderEventLog() {}

    public OrderEventLog(String orderId, String eventId, String eventType, String eventData) {
        this.orderId = orderId;
        this.eventId = eventId;
        this.eventType = eventType;
        this.eventData = eventData;
        this.occurredAt = LocalDateTime.now();
        this.loggedAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventData() {
        return eventData;
    }

    public void setEventData(String eventData) {
        this.eventData = eventData;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public LocalDateTime getLoggedAt() {
        return loggedAt;
    }

    public void setLoggedAt(LocalDateTime loggedAt) {
        this.loggedAt = loggedAt;
    }
}

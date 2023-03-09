package com.app.backend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "event_click")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventClick implements Event{
    @Id
    private String id;
    private String userId;
    private LocalDateTime clientTime;
    private String cssSelector;
    private String innerText;
    private String clientUserAgent;
    private String clientIpAddress;
    private LocalDateTime serverTime;
    @DBRef
    private Site site;

    public EventClick(String userId, LocalDateTime clientTime, String cssSelector, String innerText, String clientUserAgent, String clientIpAddress, LocalDateTime serverTime, Site site) {
        this.userId = userId;
        this.clientTime = clientTime;
        this.cssSelector = cssSelector;
        this.innerText = innerText;
        this.clientUserAgent = clientUserAgent;
        this.clientIpAddress = clientIpAddress;
        this.serverTime = serverTime;
        this.site = site;
    }
}
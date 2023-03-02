package com.app.backend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "event_click")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventClick {
    @Id
    private String id;
    private String userId;
    private LocalDateTime clientTime;
    private String cssSelector;
    private String innerText;
    private String clientIpAddress;
    private String clientUserAgent;
    private LocalDateTime serverTime;

    public EventClick(String userId, LocalDateTime clientTime, String cssSelector, String innerText, String clientIpAddress, String clientUserAgent, LocalDateTime serverTime) {
        this.userId = userId;
        this.clientTime = clientTime;
        this.cssSelector = cssSelector;
        this.innerText = innerText;
        this.clientIpAddress = clientIpAddress;
        this.clientUserAgent = clientUserAgent;
        this.serverTime = serverTime;
    }

}
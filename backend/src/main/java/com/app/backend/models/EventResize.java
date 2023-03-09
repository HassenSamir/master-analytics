package com.app.backend.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "event_resize")
public class EventResize implements Event{
    @Id
    private String id;
    private String userId;
    private int screenWidth;
    private int screenHeight;
    private LocalDateTime clientTime;
    private String clientUserAgent;
    private String ipAddress;
    private LocalDateTime serverTime;
    @DBRef
    private Site site;

    public EventResize(String userId, int screenWidth, int screenHeight, LocalDateTime clientTime, String clientUserAgent, String ipAddress, LocalDateTime serverTime,Site site) {
        this.userId = userId;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.clientTime = clientTime;
        this.clientUserAgent = clientUserAgent;
        this.ipAddress = ipAddress;
        this.serverTime = serverTime;
        this.site = site;
    }
}

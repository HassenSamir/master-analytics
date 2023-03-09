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
@Document(collection = "event_page_change")
public class EventPageChange implements EventI {
    @Id
    private String id;
    private String userId;
    private String oldPage;
    private String newPage;
    private LocalDateTime clientTime;
    private String clientUserAgent;
    private String ipAddress;
    private LocalDateTime serverTime;
    @DBRef
    private Site site;

    public EventPageChange(String userId, String oldPage, String newPage, LocalDateTime clientTime, String clientUserAgent, String ipAddress, LocalDateTime serverTime, Site site) {
        this.userId = userId;
        this.oldPage = oldPage;
        this.newPage = newPage;
        this.clientTime = clientTime;
        this.clientUserAgent = clientUserAgent;
        this.ipAddress = ipAddress;
        this.serverTime = serverTime;
        this.site = site;

    }
}
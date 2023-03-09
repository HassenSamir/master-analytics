package com.app.backend.models;

import java.time.LocalDateTime;

public interface EventI {
    String getId();
    String getUserId();
    LocalDateTime getClientTime();
    String getClientUserAgent();
    String getIpAddress();
    LocalDateTime getServerTime();
    Site getSite();
}

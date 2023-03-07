package com.app.backend.models;

import java.time.LocalDateTime;

public interface Event {
    LocalDateTime getClientTime();
    String getUserId();
}

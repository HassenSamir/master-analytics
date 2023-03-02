package com.app.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventResizeDTO {
    private String id;
    private String userId;
    private int screenWidth;
    private int screenHeight;
    private LocalDateTime clientTime;
}
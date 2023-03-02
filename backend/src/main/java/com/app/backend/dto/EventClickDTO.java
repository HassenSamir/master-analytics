package com.app.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventClickDTO {
    private String userId;
    private LocalDateTime clientTime;
    private String cssSelector;
    private String innerText;
}
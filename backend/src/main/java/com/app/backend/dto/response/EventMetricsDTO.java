package com.app.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventMetricsDTO {
    private int click;
    private int resize;
    private int page_change;
    private int custom;
    private int total;
}

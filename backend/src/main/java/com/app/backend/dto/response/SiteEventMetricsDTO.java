package com.app.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SiteEventMetricsDTO {
    private String site;
    private int click;
    private int resize;
    private int pageChange;
    private int custom;
    private int total;
}

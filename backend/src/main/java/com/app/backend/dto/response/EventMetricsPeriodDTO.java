package com.app.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventMetricsPeriodDTO {

    private String period;
    private Integer year;
    private List<EventMetricsDataDTO> data;
}

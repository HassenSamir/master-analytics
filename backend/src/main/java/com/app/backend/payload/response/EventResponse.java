package com.app.backend.payload.response;

import com.app.backend.models.EventData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventResponse {
    private String type;
    private EventData data;
    private Pageable pageable;
}

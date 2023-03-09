package com.app.backend.payload.response;

import com.app.backend.models.EventTypeData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventTypeResponse {
    private String type;
    private EventTypeData data;
    private Pageable pageable;
}

package com.app.backend.dto.response;

import com.app.backend.models.EventI;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventResponseDTO {
    private String type;
    private List<EventI> data;
    private Pageable pageable;

    private long totalElements;
    private long totalPages;
}

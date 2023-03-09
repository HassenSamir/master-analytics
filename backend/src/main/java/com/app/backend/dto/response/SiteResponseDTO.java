package com.app.backend.dto.response;

import com.app.backend.models.Site;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SiteResponseDTO {
    private String id;
    private String name;
    private String url;
    private String description;
    private LocalDateTime clientTime;
    private String apiKey;
    private LocalDateTime creationDate;

    public SiteResponseDTO(Site site) {
        this.id = site.getId();
        this.name = site.getName();
        this.url = site.getUrl();
        this.description = site.getDescription();
        this.clientTime = site.getClientTime();
        this.apiKey = site.getApiKey();
    }
}

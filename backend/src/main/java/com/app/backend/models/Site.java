package com.app.backend.models;

import java.time.LocalDateTime;
import java.util.Date;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "sites")
public class Site {
    @Id
    private String id;

    @NotBlank
    @Size(max = 120)
    private String name;

    @NotBlank
    private String url;

    @NotBlank
    private String description;

    @NotBlank
    private String apiKey;

    @NotBlank
    private Date creationDate;

    @NotBlank
    private String userId;

    public Site(String name, String url, String description, String apiKey, Date creationDate, String userId) {
        this.name = name;
        this.url = url;
        this.description = description;
        this.apiKey = apiKey;
        this.creationDate = creationDate;
        this.userId = userId;
    }

}

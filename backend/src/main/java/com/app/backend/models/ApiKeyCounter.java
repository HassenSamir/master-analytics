package com.app.backend.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "api_keys")
public class ApiKeyCounter {
    @Id
    private String id;
    private String apiKey;
    private int counter;
    private LocalDate lastEventDate;
    private int dailyLimit;

    public ApiKeyCounter(String apiKey, LocalDate lastEventDate) {
        this.apiKey = apiKey;
        this.lastEventDate = lastEventDate;
        this.counter = 0;
        this.dailyLimit = 10;
    }

    public ApiKeyCounter(String apiKey, int counter, LocalDate lastEventDate, int dailyLimit) {
        this.apiKey = apiKey;
        this.lastEventDate = lastEventDate;
        this.counter = counter;
        this.dailyLimit = dailyLimit;
    }

}

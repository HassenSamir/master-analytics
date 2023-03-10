package com.app.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteDTO {
    @NotBlank
    @Size(max = 120)
    private String name;

    @NotBlank
    private String url;

    @NotBlank
    private String description;


}

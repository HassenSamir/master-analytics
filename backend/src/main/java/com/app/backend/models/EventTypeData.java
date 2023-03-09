package com.app.backend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventTypeData {
    private List<EventClick> clickEvents;
    private List<EventPageChange> pageChangeEvents;
    private List<EventResize> resizeEvents;

}
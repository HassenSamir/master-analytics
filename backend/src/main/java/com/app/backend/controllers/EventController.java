package com.app.backend.controllers;

import com.app.backend.dto.EventClickDTO;
import com.app.backend.dto.EventPageChangeDTO;
import com.app.backend.dto.EventResizeDTO;
import com.app.backend.models.*;
import com.app.backend.payload.response.EventResponse;
import com.app.backend.repository.UserRepository;
import com.app.backend.services.EventServices;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventServices eventService;

    @Autowired
    UserRepository userRepository;


    // Structure this with object click, pagechange, resize rathen than an Array because it is hard to differenciate
    //paginate because they will be a lot of data
    //add params like date etc
    @GetMapping("/")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<EventResponse> getAllEvents(@RequestParam(name = "type", required = false) String type) {
        List<EventClick> clickEvents = eventService.findAllClickEvents();
        List<EventPageChange> pageChangeEvents = eventService.findAllPageChangeEvents();
        List<EventResize> resizeEvents = eventService.findAllResizeEvents();

        if (type != null) {
            return switch (type) {
                case "click" -> ResponseEntity.ok().body(new EventResponse(type, (EventData) clickEvents));
                case "page_change" -> ResponseEntity.ok().body(new EventResponse(type, (EventData) pageChangeEvents));
                case "resize" -> ResponseEntity.ok().body(new EventResponse(type, (EventData) resizeEvents));
                default -> throw new IllegalArgumentException("Invalid event type: " + type);
            };
        } else {
            EventData eventData = new EventData(clickEvents, pageChangeEvents, resizeEvents);
            EventResponse eventResponse = new EventResponse("all", eventData);
            return ResponseEntity.ok().body(eventResponse);
        }
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getAllEventsByTypeAndUserId(@PathVariable String userId,@RequestParam(name = "type", required = false, defaultValue = "") String type) throws IOException {
        return eventService.findAllEventsByTypeAndUserId(type, userId);
    }
    @GetMapping("/site/{siteId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getAllEventsByTypeAndSiteId(@PathVariable String siteId,@RequestParam(name = "type", required = false, defaultValue = "") String type) throws IOException {
        return eventService.findAllEventsByTypeAndSiteId(type, siteId);
    }

    //Get All Events for one site
    @PostMapping("/click/{apiKey}")
    public ResponseEntity<?> createClickEvent(@Valid @RequestBody EventClickDTO clickEventDTO, @PathVariable String apiKey, HttpServletRequest request) throws IOException {
        return eventService.createClickEvent(clickEventDTO, request, apiKey);
    }

    @PostMapping("/page_change/{apiKey}")
    public ResponseEntity<?>  createPageChangeEvent(@Valid @RequestBody EventPageChangeDTO pageChangeEventDTO, @PathVariable String apiKey, HttpServletRequest request) throws IOException {
        return eventService.createPageChangeEvent(pageChangeEventDTO, request, apiKey);
    }

    @PostMapping("/resize/{apiKey}")
    public ResponseEntity<?> createResizeEvent(@Valid @RequestBody EventResizeDTO resizeEventDTO, @PathVariable String apiKey, HttpServletRequest request) throws IOException {
        return eventService.createResizeEvent(resizeEventDTO, request, apiKey);
    }

}

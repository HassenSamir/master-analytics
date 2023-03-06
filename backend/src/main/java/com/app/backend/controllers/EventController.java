package com.app.backend.controllers;

import com.app.backend.dto.EventClickDTO;
import com.app.backend.dto.EventPageChangeDTO;
import com.app.backend.dto.EventResizeDTO;
import com.app.backend.models.*;
import com.app.backend.payload.response.EventResponse;
import com.app.backend.repository.UserRepository;
import com.app.backend.security.services.UserDetailsImpl;
import com.app.backend.services.EventServices;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.io.IOException;
import java.net.URI;
import java.nio.file.AccessDeniedException;
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



    // Handle types
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<EventResponse> getAllEventsByTypeAndUserId(@PathVariable String userId,@RequestParam(name = "type", required = true) String type) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        boolean isUserRoleOnly = authorities.size() == 1 && authorities.contains(new SimpleGrantedAuthority("ROLE_USER"));

        if (isUserRoleOnly && !((UserDetailsImpl) auth.getPrincipal()).getId().equals(userId)) {
            throw new AccessDeniedException("You are not authorized to access this resource");
        }

        // Vérifier que l'utilisateur existe
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IOException("User with ID " + userId + " not found");
        }

        EventData data = new EventData();
        if (type.equalsIgnoreCase("click")) {
            List<EventClick> clickEvents = eventService.findAllClickEventsByUserId(userId);
            data.setClickEvents(clickEvents);

        } else if (type.equalsIgnoreCase("page_change")) {
            List<EventPageChange> pageChangesEvents = eventService.findAllPageChangeEventsByUserId(userId);
            data.setPageChangeEvents(pageChangesEvents);
        } else if (type.equalsIgnoreCase("resize")) {
            List<EventResize> resizeEvents = eventService.findAllResizeEventsByUserId(userId);
            data.setResizeEvents(resizeEvents);
        } else {
            throw new IllegalArgumentException("Invalid event type: " + type);
        }

        EventResponse response = new EventResponse(type, data);
        return ResponseEntity.ok().body(response);
    }



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

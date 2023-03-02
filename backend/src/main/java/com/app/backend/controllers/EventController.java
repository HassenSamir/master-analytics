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
import java.util.stream.Collectors;

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


    // Maybe update this to handle Array Of Event
    // Handle Types ?
    @PostMapping("/click")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<EventClick> createClickEvent(@Valid @RequestBody EventClickDTO clickEventDTO, HttpServletRequest request) throws IOException {
        EventClick eventClick = eventService.createClickEvent(clickEventDTO, request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(eventClick.getId()).toUri();
        return ResponseEntity.created(location).body(eventClick);
    }

    @PostMapping("/page_change")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<EventPageChange> createPageChangeEvent(@Valid @RequestBody EventPageChangeDTO pageChangeEventDTO, HttpServletRequest request) throws IOException {
        EventPageChange eventPageChange= eventService.createPageChangeEvent(pageChangeEventDTO, request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(eventPageChange.getId()).toUri();
        return ResponseEntity.created(location).body(eventPageChange);
    }

    @PostMapping("/resize")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<EventResize> createResizeEvent(@Valid @RequestBody EventResizeDTO resizeEventDTO, HttpServletRequest request) throws IOException {
        EventResize eventResize = eventService.createResizeEvent(resizeEventDTO, request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(eventResize.getId()).toUri();
        return ResponseEntity.created(location).body(eventResize);
    }

}

package com.app.backend.controllers;

import com.app.backend.dto.EventClickDTO;
import com.app.backend.dto.EventPageChangeDTO;
import com.app.backend.dto.EventResizeDTO;
import com.app.backend.models.*;
import com.app.backend.payload.response.EventResponse;
import com.app.backend.repository.UserRepository;
import com.app.backend.services.EventMetricsService;
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
@RequestMapping("/api/events/metrics")
public class EventMetricsController {

    @Autowired
    private EventMetricsService eventMetricsService;

    @Autowired
    UserRepository userRepository;


    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getEventsMetricsByUserId(@PathVariable String userId) {
        return eventMetricsService.getEventMetrics(userId);
    }

    @GetMapping("/period/user/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getEventsMetricsByUserIdAndPeriod(@PathVariable String userId, @RequestParam(required = true) String period, @RequestParam(required = false) Integer year) {
        return eventMetricsService.getEventMetricsByPeriodAndUserId(userId, period, year);
    }

    @GetMapping("/user/{userId}/site/{siteId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getEventMetricsByUserIdAndSiteId(@PathVariable String userId, @PathVariable String siteId) {
        return eventMetricsService.getEventMetricsByUserIdAndSiteId(userId, siteId);
    }

}

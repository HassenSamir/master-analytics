package com.app.backend.utils;

import com.app.backend.handler.ErrorResponse;
import com.app.backend.models.EventI;
import com.app.backend.models.Site;
import com.app.backend.models.User;
import com.app.backend.repository.SiteRepository;
import com.app.backend.repository.UserRepository;
import com.app.backend.security.services.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {

    public static ResponseEntity<?> checkIfUserExistById(UserRepository userRepository, String userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Not Found", "User not found with id: " + userId));
        }
        return null;
    }
    public static boolean isAuthorized(String userId, UserRepository userRepository) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            return false;
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isModerator = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MODERATOR"));
        return isAdmin || isModerator || ((UserDetailsImpl) authentication.getPrincipal()).getId().equals(userId);
    }

    public static ResponseEntity<?> checkSiteApiKey(HttpServletRequest request, String apiKey, SiteRepository siteRepository) throws MalformedURLException {

        // Vérifier si l'apiKey existe dans la base de données
        Optional<Site> siteOptional = siteRepository.findByApiKey(apiKey);
        if (siteOptional.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Not Found", "Site with apiKey " + apiKey + " not found"));
        }
        Site site = siteOptional.get();

        // Vérifier si l'URL de la requête correspond à l'URL du site
        String requestUrl = request.getRequestURL().toString();
        String siteUrl = site.getUrl();
        if (isNotSameDomain(siteUrl,requestUrl)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Not Found", "Invalid request URL "+ requestUrl));
        }

        return null;
    }

    public static boolean isNotSameDomain(String url1, String url2) throws MalformedURLException {
        URL u1 = new URL(url1);
        URL u2 = new URL(url2);

        String host1 = u1.getHost();
        String host2 = u2.getHost();

        String protocol1 = u1.getProtocol();
        String protocol2 = u2.getProtocol();

        return !(host1.equals(host2) && protocol1.equals(protocol2));
    }

    public static Map<String, Integer> groupEventsByPeriod(List<? extends EventI> events, String period) {
        Map<String, Integer> result = new LinkedHashMap<>();

        switch (period) {
            case "month" -> {
                Map<String, Integer> groupedMonths = events.stream()
                        .collect(Collectors.groupingBy(event -> event.getClientTime().getMonth().toString(), Collectors.summingInt(e -> 1)));
                List<String> months = Arrays.stream(Month.values()).map(Month::toString).toList();
                for (String month : months) {
                    result.put(month, groupedMonths.getOrDefault(month, 0));
                }
            }
            case "week" -> {
                Map<String, Integer> groupedWeeks = events.stream()
                        .collect(Collectors.groupingBy(event -> event.getClientTime().getDayOfWeek().toString(), Collectors.summingInt(e -> 1)));
                List<String> daysOfWeek = Arrays.stream(DayOfWeek.values()).map(DayOfWeek::toString).toList();
                for (String dayOfWeek : daysOfWeek) {
                    result.put(dayOfWeek, groupedWeeks.getOrDefault(dayOfWeek, 0));
                }
            }
            case "day" -> {
                Map<String, Integer> groupedDays = events.stream()
                        .collect(Collectors.groupingBy(event -> String.format("%02d", event.getClientTime().getHour()), Collectors.summingInt(e -> 1)));
                for (int i = 0; i <= 23; i++) {
                    result.put(String.format("%02d", i), groupedDays.getOrDefault(String.format("%02d", i), 0));
                }
            }
            default -> throw new IllegalArgumentException("Invalid period value: " + period);
        }

        return result;
    }
}

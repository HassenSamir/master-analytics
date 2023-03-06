package com.app.backend.utils;

import com.app.backend.handler.ErrorResponse;
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
import java.util.Optional;

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
}

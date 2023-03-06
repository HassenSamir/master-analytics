package com.app.backend.services;

import com.app.backend.controllers.SiteController;
import com.app.backend.dto.SiteDTO;
import com.app.backend.handler.ErrorResponse;
import com.app.backend.models.Site;
import com.app.backend.models.User;
import com.app.backend.repository.SiteRepository;
import com.app.backend.repository.UserRepository;
import com.app.backend.security.config.ApiKeyGenerator;
import com.app.backend.security.services.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import java.net.URI;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SiteServices {

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> createSite(SiteDTO siteDto, String userId)  {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Not Found", "User not found with id: " + userId));
        }
        User user = optionalUser.get();


        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isModerator = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MODERATOR"));
        if (!isAdmin && !isModerator && !((UserDetailsImpl) authentication.getPrincipal()).getId().equals(userId)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", "You are not authorized to access this resource"));
        }


        Site site = new Site();
        site.setUser(user);
        site.setName(siteDto.getName());
        site.setUrl(siteDto.getUrl());
        site.setDescription(siteDto.getDescription());
        site.setClientTime(LocalDateTime.now());

        String apiKey = ApiKeyGenerator.generateApiKey();
        site.setApiKey(apiKey);
        site.setCreationDate(new Date());

        /*
        HttpStatus urlStatus = isValidUrl(site.getUrl());
        if (urlStatus != HttpStatus.OK) {
            throw new ResponseStatusException(urlStatus, "Invalid URL");
        }*/

       /* Optional<Site> optionalSite = siteRepository.findByUserIdAndName(userId, site.getName());
        if (optionalSite.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Site name already exists for user");
        }*/

        if (site.getDescription() == null || site.getDescription().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Site description is required");
        }

        Site savedSite = siteRepository.save(site);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedSite);
    }
    public void deleteSite(String id) throws Exception {
        // Vérifier si l'ID du site est présent
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Site ID is required");
        }

        // Vérifier si le site existe
        Optional<Site> optionalSite = siteRepository.findById(id);
        if (optionalSite.isEmpty()) {
            throw new RuntimeException("Site not found with ID: " + id);
        }

        Site site = optionalSite.get();

        // Vérification si l'utilisateur connecté est propriétaire du site
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String loggedInUserId = authentication.getName();

        if (!site.getUser().getId().equals(loggedInUserId) && !isAdmin) {
            throw new AccessDeniedException("You are not authorized to delete this site");
        }

        // Supprimer le site
        siteRepository.deleteById(id);
    }

    public List<Site> getSitesByUserId(String userId) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Vérifier si l'utilisateur existe
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new Exception("User not found with id: " + userId);
        }

        // Vérifier si l'utilisateur connecté est propriétaire des sites ou s'il est modérateur ou administrateur
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isModerator = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MODERATOR"));
        if (!isAdmin && !isModerator && !((UserDetailsImpl) authentication.getPrincipal()).getId().equals(userId)) {
            throw new AccessDeniedException("You are not authorized to access this resource");
        }

        return siteRepository.findAllByUserId(userId);
    }

    public Site updateSite(String id, Site updatedSite) throws Exception {
        // Vérifier si le site existe
        Optional<Site> optionalSite = siteRepository.findById(id);
        if (optionalSite.isEmpty()) {
            throw new Exception("Site not found with id: " + id);
        }

        Site site = optionalSite.get();

        // Vérifier si le nom du site est unique pour l'utilisateur
        if (siteRepository.findByUserIdAndName(site.getUser().getId(), updatedSite.getName()).isPresent()) {
            throw new Exception("Site name already exists for user");
        }

        // Vérifier si l'URL du site est valide
        HttpStatus urlStatus = isValidUrl(site.getUrl());
        if (urlStatus != HttpStatus.OK) {
            throw new ResponseStatusException(urlStatus, "Invalid URL");
        }

        // Vérifier si la description du site est présente
        if (updatedSite.getDescription() == null || updatedSite.getDescription().isEmpty()) {
            throw new Exception("Site description is required");
        }

        // Mettre à jour les informations du site
        site.setName(updatedSite.getName());
        site.setUrl(updatedSite.getUrl());
        site.setDescription(updatedSite.getDescription());

        return siteRepository.save(site);
    }


    // Méthode utilitaire pour vérifier si une URL est valide
    public static HttpStatus isValidUrl(String url) {
        try {
            URI uri = new URI(url);

            if (!uri.isAbsolute()) {
                return HttpStatus.BAD_REQUEST;
            }

            String scheme = uri.getScheme();
            if (scheme == null || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
                return HttpStatus.BAD_REQUEST;
            }

            WebClient client = WebClient.builder().build();

            Mono<ClientResponse> result = client.head().uri(url).exchangeToMono(Mono::just);

            HttpStatus statusCode = (HttpStatus) result.map(ClientResponse::statusCode).block();

            assert statusCode != null;
            return statusCode.is2xxSuccessful() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;

        } catch (Exception e) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    public static boolean isUrlReachable(String url) {
        WebClient client = WebClient.builder().build();

        try {
            Mono<ClientResponse> result = client.head().uri(url).exchangeToMono(Mono::just);

            String responseBody = result.map(response -> {
                HttpStatus status = (HttpStatus) response.statusCode();
                if (status.is2xxSuccessful()) {
                    return "Success";
                } else {
                    return "Error";
                }
            }).block();

            assert responseBody != null;
            return responseBody.equals("Success");

        } catch (Exception e) {
            return false;
        }
    }
}

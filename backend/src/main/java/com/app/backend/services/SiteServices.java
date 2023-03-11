package com.app.backend.services;

import com.app.backend.dto.SiteDTO;
import com.app.backend.dto.SiteUpdateDTO;
import com.app.backend.handler.ErrorResponse;
import com.app.backend.models.Site;
import com.app.backend.models.User;
import com.app.backend.repository.SiteRepository;
import com.app.backend.repository.UserRepository;
import com.app.backend.security.config.ApiKeyGenerator;
import com.app.backend.security.services.UserDetailsImpl;
import com.app.backend.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.net.URI;
import java.util.Date;
import java.util.Optional;

@Service
public class SiteServices {

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> createSite(SiteDTO siteDto, String userId)  {
        // Vérifier si l'utilisateur existe
        ResponseEntity<?> isUserOnDb = Utils.checkIfUserExistById(userRepository, userId);
        if (isUserOnDb != null) {
            return isUserOnDb;
        }
        // Vérifier si l'utilisateur est autorisé à accéder à la ressource
        if (!Utils.isAuthorized(userId, userRepository)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", "You are not authorized to access this resource"));
        }

        Optional<User> optionalUser = userRepository.findById(userId);
        User user = optionalUser.get();

        Site site = new Site();
        site.setUserId(user.getId());
        site.setName(siteDto.getName());
        site.setUrl(siteDto.getUrl());
        site.setDescription(siteDto.getDescription());

        String apiKey = ApiKeyGenerator.generateApiKey();
        site.setApiKey(apiKey);
        site.setCreationDate(new Date());


        HttpStatus urlStatus = isValidUrl(site.getUrl());
        if (urlStatus != HttpStatus.OK) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", "Invalid URL"));
        }

        Optional<Site> optionalSite = siteRepository.findByUserIdAndName(userId, site.getName());
        if (optionalSite.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", "Site name already exists for user"));
        }

        if (site.getDescription() == null || site.getDescription().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", "Site description is required"));
        }

        Site savedSite = siteRepository.save(site);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedSite);
    }
    public ResponseEntity<?> deleteSite(String id)  {
        // Vérifier si l'ID du site est présent
        if (id == null || id.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", "Site ID is required"));
        }

        // Vérifier si le site existe
        Optional<Site> optionalSite = siteRepository.findById(id);
        if (optionalSite.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Not Found", "Site not found with ID: " + id));
        }
        Site site = optionalSite.get();

        // Vérification si l'utilisateur connecté est propriétaire du site
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        // Extract user id from token
        String userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();

        if (!site.getUserId().equals(userId) && !isAdmin) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", "You are not authorized to delete this site"));
        }

        // Supprimer le site
        siteRepository.deleteById(id);

        return ResponseEntity.status(HttpStatus.OK).body(site);
    }

    public ResponseEntity<?> getSitesByUserId(String userId) {
        // Vérifier si l'utilisateur existe
        ResponseEntity<?> isUserOnDb = Utils.checkIfUserExistById(userRepository, userId);
        if (isUserOnDb != null) {
            return isUserOnDb;
        }


        if (!Utils.isAuthorized(userId, userRepository)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", "You are not authorized to access this resource"));
        }

        return ResponseEntity.status(HttpStatus.OK).body(siteRepository.findAllByUserId(userId));
    }

    public ResponseEntity<?> updateSite(String id, SiteUpdateDTO updatedSite) {

        // Vérifier si le site existe
        Optional<Site> optionalSite = siteRepository.findById(id);
        if (optionalSite.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Not Found", "Site not found with id: " + id));

        }

        Site site = optionalSite.get();


        // Vérification si l'utilisateur connecté est propriétaire du site
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        // Extract user id from token
        String userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();

        if (!site.getUserId().equals(userId) && !isAdmin) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", "You are not authorized to delete this site"));
        }


        // Vérifier si le nom du site est unique pour l'utilisateur
        Optional<Site> optionalSiteByName = siteRepository.findByUserIdAndName(site.getUserId(), site.getName());
        if (optionalSiteByName.isPresent() && !optionalSiteByName.get().getId().equals(id)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", "Site name already exists for user"));

        }

        // Vérifier si au moins une propriété de SiteUpdateDTO est présente
        if ((updatedSite.getName() == null || updatedSite.getName().isBlank()) && (updatedSite.getUrl() == null || updatedSite.getUrl().isBlank())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", "At least one field must be present for update"));
        }

        // Vérifier si l'élément modifié est différent de celui qui existe déjà
        if (updatedSite.getName() != null && updatedSite.getName().equals(site.getName()) && updatedSite.getUrl() != null && updatedSite.getUrl().equals(site.getUrl())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", "Site name and url are the same as the existing site"));
        }

        HttpStatus urlStatus = isValidUrl(updatedSite.getUrl());
        if (urlStatus != HttpStatus.OK) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", "Invalid URL"));
        }

        // Mettre à jour les informations du site
        if (updatedSite.getName() != null && !updatedSite.getName().isBlank()) {
            site.setName(updatedSite.getName());
        }
        if (updatedSite.getUrl() != null && !updatedSite.getUrl().isBlank()) {
            site.setUrl(updatedSite.getUrl());
        }

        siteRepository.save(site);
        return ResponseEntity.ok("Site updated successfully.");
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

}

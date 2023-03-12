package com.app.backend.services;

import com.app.backend.dto.EventClickDTO;
import com.app.backend.dto.EventPageChangeDTO;
import com.app.backend.dto.EventResizeDTO;
import com.app.backend.handler.ErrorResponse;
import com.app.backend.models.*;
import com.app.backend.payload.response.EventTypeResponse;
import com.app.backend.repository.*;
import com.app.backend.security.services.UserDetailsImpl;
import com.app.backend.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class EventServices {
    private final Logger logger = LoggerFactory.getLogger(EventMetricsService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private EventClickRepository eventClickRepository;
    @Autowired
    private ApiKeyCounterRepository apiKeyCounterRepository;

    @Autowired
    private EventPageChangeRepository eventPageChangeRepository;

    @Autowired
    private EventResizeRepository eventResizeRepository;



    public ResponseEntity<?> findAllEventsByTypeAndUserId(String type, String userId, int page,int size) {
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

        EventTypeData data = new EventTypeData();
        EventTypeResponse response;
        Pageable pageable = PageRequest.of(page, size);

        if (type.isEmpty()){
            // Récupérer une page triée et paginée d'événements de tous les types
            Page<EventClick> clickEventsPage = eventClickRepository.findAllByUserId(userId, pageable);
            Page<EventPageChange> pageChangeEventsPage = eventPageChangeRepository.findAllByUserId(userId, pageable);
            Page<EventResize> resizeEventsPage = eventResizeRepository.findAllByUserId(userId, pageable);

            // Ajouter les événements récupérés aux données de réponse
            data.setClickEvents(clickEventsPage.getContent());
            data.setPageChangeEvents(pageChangeEventsPage.getContent());
            data.setResizeEvents(resizeEventsPage.getContent());
            response = new EventTypeResponse("all", data, clickEventsPage.getPageable());
        } else if (type.equalsIgnoreCase("click")) {
            Page<EventClick> clickEventsPage = eventClickRepository.findAllByUserId(userId, pageable);
            // Ajouter les événements récupérés aux données de réponse
            data.setClickEvents(clickEventsPage.getContent());
            response = new EventTypeResponse(type, data, clickEventsPage.getPageable());
        } else if (type.equalsIgnoreCase("page_change")) {
            Page<EventPageChange> pageChangeEventsPage = eventPageChangeRepository.findAllByUserId(userId, pageable);
            // Ajouter les événements récupérés aux données de réponse
            data.setPageChangeEvents(pageChangeEventsPage.getContent());
            response = new EventTypeResponse(type, data, pageChangeEventsPage.getPageable());
        } else if (type.equalsIgnoreCase("resize")) {
            Page<EventResize> resizeEventsPage = eventResizeRepository.findAllByUserId(userId, pageable);
            // Ajouter les événements récupérés aux données de réponse
            data.setResizeEvents(resizeEventsPage.getContent());
            response = new EventTypeResponse(type, data,resizeEventsPage.getPageable());
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", "Invalid event type: " + type));
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    public ResponseEntity<?> findAllEventsByTypeAndSiteId(String type, String siteId, int page,int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Extract user id from token
        String userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();

        // Check if the site exists and is associated with the user
        Optional<Site> siteOptional = siteRepository.findByIdAndUserId(siteId, userId);
        if (siteOptional.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Not Found", "Site not found or unauthorized"));
        }
        Site site = siteOptional.get();

        EventTypeData data = new EventTypeData();
        EventTypeResponse response;
        Pageable pageable = PageRequest.of(page, size);

        if (type.isEmpty()){

            Page<EventClick> clickEventsPage = eventClickRepository.findAllBySite(site, pageable);
            Page<EventPageChange> pageChangeEventsPage = eventPageChangeRepository.findAllBySite(site, pageable);
            Page<EventResize> resizeEventsPage = eventResizeRepository.findAllBySite(site, pageable);

            data.setClickEvents(clickEventsPage.getContent());
            data.setPageChangeEvents(pageChangeEventsPage.getContent());
            data.setResizeEvents(resizeEventsPage.getContent());
            response = new EventTypeResponse("all", data, clickEventsPage.getPageable());
        } else if (type.equalsIgnoreCase("click")) {
            Page<EventClick> clickEventsPage = eventClickRepository.findAllBySite(site, pageable);
            data.setClickEvents(clickEventsPage.getContent());
            response = new EventTypeResponse(type, data, clickEventsPage.getPageable());
        } else if (type.equalsIgnoreCase("page_change")) {
            Page<EventPageChange> pageChangeEventsPage = eventPageChangeRepository.findAllBySite(site, pageable);
            data.setPageChangeEvents(pageChangeEventsPage.getContent());
            response = new EventTypeResponse(type, data, pageChangeEventsPage.getPageable());
        } else if (type.equalsIgnoreCase("resize")) {
            Page<EventResize> resizeEventsPage = eventResizeRepository.findAllBySite(site, pageable);
            data.setResizeEvents(resizeEventsPage.getContent());
            response = new EventTypeResponse(type, data, resizeEventsPage.getPageable());
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", "Invalid event type: " + type));
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // CREATE
    public ResponseEntity<?> createClickEvents(List<EventClickDTO> clickEventsDTO, HttpServletRequest request, String apiKey) throws IOException {
        // Vérifier si l'apiKey existe dans la base de données
        // et si l'URL de la requête correspond à l'URL du site
        ResponseEntity<?> checkIfApiKeyMatchSiteInDbAndIfReqUrlIsValid = Utils.checkSiteApiKey(request, apiKey, siteRepository);
        if (checkIfApiKeyMatchSiteInDbAndIfReqUrlIsValid != null) {
            return checkIfApiKeyMatchSiteInDbAndIfReqUrlIsValid;
        }

        Optional<Site> siteOptional = siteRepository.findByApiKey(apiKey);
        Site site = siteOptional.get();

        // Vérifier si la liste d'événements est vide ou nulle
        if (ObjectUtils.isEmpty(clickEventsDTO)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", "Click events list is required"));
        }

        // Créer un nouvel objet événement de clic pour chaque élément de la liste
        List<EventClick> clickEvents = new ArrayList<>();
        for (EventClickDTO clickEventDTO : clickEventsDTO) {
            // Vérifier si le sélecteur CSS est non nul et non vide
            if (ObjectUtils.isEmpty(clickEventDTO.getCssSelector())) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", "CSS selector is required"));
            }

            // Vérifier si le texte interne est non nul et non vide
            if (ObjectUtils.isEmpty(clickEventDTO.getInnerText())) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", "Inner text is required"));
            }

            // Récupérer l'objet ApiKeyCounter correspondant à l'API key
            ApiKeyCounter apiKeyCounter = apiKeyCounterRepository.findByApiKey(apiKey);

            // Vérifier si le jour a changé
            if (apiKeyCounter.getLastEventDate() == null || !apiKeyCounter.getLastEventDate().equals(LocalDate.now())) {
                apiKeyCounter.setCounter(0); // Réinitialiser le compteur à 0
            }

            // Vérifier si la limite quotidienne a été atteinte
            if (apiKeyCounter.getCounter() >= apiKeyCounter.getDailyLimit()) {
                return ResponseEntity
                        .status(HttpStatus.TOO_MANY_REQUESTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new ErrorResponse(HttpStatus.TOO_MANY_REQUESTS.value(), "Too Many Requests", "Daily limit for API key has been reached"));
            }

            // Augmenter le compteur de l'objet ApiKeyCounter et enregistrer dans la base de données
            apiKeyCounter.setCounter(apiKeyCounter.getCounter() + 1);
            apiKeyCounter.setLastEventDate(LocalDate.now());
            apiKeyCounterRepository.save(apiKeyCounter);

            EventClick clickEvent = new EventClick(
                    site.getUserId(),
                    clickEventDTO.getClientTime(),
                    clickEventDTO.getCssSelector(),
                    clickEventDTO.getInnerText(),
                    request.getHeader("User-Agent"),
                    request.getRemoteAddr(),
                    LocalDateTime.now(),
                    site
            );
            clickEvents.add(clickEvent);
        }

        // Enregistrer les événements de clic dans la base de données
        eventClickRepository.saveAll(clickEvents);

        return ResponseEntity.status(HttpStatus.CREATED).body(clickEvents);
    }

    public ResponseEntity<?> createPageChangeEvents(List<EventPageChangeDTO> pageChangeEventDTOs, HttpServletRequest request, String apiKey) throws IOException {
        // Vérifier si l'apiKey existe dans la base de données
        // et si l'URL de la requête correspond à l'URL du site
        ResponseEntity<?> checkIfApiKeyMatchSiteInDbAndIfReqUrlIsValid = Utils.checkSiteApiKey(request, apiKey, siteRepository);
        if (checkIfApiKeyMatchSiteInDbAndIfReqUrlIsValid != null) {
            return checkIfApiKeyMatchSiteInDbAndIfReqUrlIsValid;
        }

        Optional<Site> siteOptional = siteRepository.findByApiKey(apiKey);
        Site site = siteOptional.get();

        List<EventPageChange> pageChangeEvents = new ArrayList<>();
        for (EventPageChangeDTO pageChangeEventDTO : pageChangeEventDTOs) {
            // Vérifier si Old page est non nul et non vide
            if (ObjectUtils.isEmpty(pageChangeEventDTO.getOldPage())) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", "Old page is required"));
            }
            // Vérifier si New page est non nul et non vide
            if (ObjectUtils.isEmpty(pageChangeEventDTO.getNewPage())) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", "New page is required"));
            }

            // Récupérer l'objet ApiKeyCounter correspondant à l'API key
            ApiKeyCounter apiKeyCounter = apiKeyCounterRepository.findByApiKey(apiKey);

            // Vérifier si le jour a changé
            if (apiKeyCounter.getLastEventDate() == null || !apiKeyCounter.getLastEventDate().equals(LocalDate.now())) {
                apiKeyCounter.setCounter(0); // Réinitialiser le compteur à 0
            }

            // Vérifier si la limite quotidienne a été atteinte
            if (apiKeyCounter.getCounter() >= apiKeyCounter.getDailyLimit()) {
                return ResponseEntity
                        .status(HttpStatus.TOO_MANY_REQUESTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new ErrorResponse(HttpStatus.TOO_MANY_REQUESTS.value(), "Too Many Requests", "Daily limit for API key has been reached"));
            }

            // Augmenter le compteur de l'objet ApiKeyCounter et enregistrer dans la base de données
            apiKeyCounter.setCounter(apiKeyCounter.getCounter() + 1);
            apiKeyCounter.setLastEventDate(LocalDate.now());
            apiKeyCounterRepository.save(apiKeyCounter);

            // Créer un nouvel objet événement page change
            EventPageChange pageChangeEvent = new EventPageChange(
                    site.getUserId(),
                    pageChangeEventDTO.getOldPage(),
                    pageChangeEventDTO.getNewPage(),
                    pageChangeEventDTO.getClientTime(),
                    request.getHeader("User-Agent"),
                    request.getRemoteAddr(),
                    LocalDateTime.now(),
                    site
            );

            // Ajouter l'événement page change à la liste des événements de changement de page
            pageChangeEvents.add(pageChangeEvent);
        }

        // Enregistrer les événements de changement de page
        eventPageChangeRepository.saveAll(pageChangeEvents);

        return ResponseEntity.status(HttpStatus.CREATED).body(pageChangeEvents);
    }

    public ResponseEntity<?> createResizeEvents(List<EventResizeDTO> eventResizeDTOs, HttpServletRequest request, String apiKey) throws IOException {
        // Vérifier si l'apiKey existe dans la base de données
        // et si l'URL de la requête correspond à l'URL du site
        ResponseEntity<?> checkIfApiKeyMatchSiteInDbAndIfReqUrlIsValid = Utils.checkSiteApiKey(request, apiKey, siteRepository);
        if (checkIfApiKeyMatchSiteInDbAndIfReqUrlIsValid != null) {
            return checkIfApiKeyMatchSiteInDbAndIfReqUrlIsValid;
        }

        Optional<Site> siteOptional = siteRepository.findByApiKey(apiKey);
        Site site = siteOptional.get();


        // Vérifier si la liste d'événements de resize n'est pas vide
        if (eventResizeDTOs.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", "No resize events to save"));
        }

        List<EventResize> eventResizes = new ArrayList<>();

        // Parcourir la liste d'événements de resize et les ajouter à la liste à enregistrer
        for (EventResizeDTO eventResizeDTO : eventResizeDTOs) {
            // Vérifier si Screen width est non nul et non vide
            if (ObjectUtils.isEmpty(eventResizeDTO.getScreenWidth())) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", "Screen width is required"));
            }
            // Vérifier si Screen height est non nul et non vide
            if (ObjectUtils.isEmpty(eventResizeDTO.getScreenHeight())) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", "Screen height is required"));
            }

            // Récupérer l'objet ApiKeyCounter correspondant à l'API key
            ApiKeyCounter apiKeyCounter = apiKeyCounterRepository.findByApiKey(apiKey);

            // Vérifier si le jour a changé
            if (apiKeyCounter.getLastEventDate() == null || !apiKeyCounter.getLastEventDate().equals(LocalDate.now())) {
                apiKeyCounter.setCounter(0); // Réinitialiser le compteur à 0
            }

            // Vérifier si la limite quotidienne a été atteinte
            if (apiKeyCounter.getCounter() >= apiKeyCounter.getDailyLimit()) {
                return ResponseEntity
                        .status(HttpStatus.TOO_MANY_REQUESTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new ErrorResponse(HttpStatus.TOO_MANY_REQUESTS.value(), "Too Many Requests", "Daily limit for API key has been reached"));
            }

            // Augmenter le compteur de l'objet ApiKeyCounter et enregistrer dans la base de données
            apiKeyCounter.setCounter(apiKeyCounter.getCounter() + 1);
            apiKeyCounter.setLastEventDate(LocalDate.now());
            apiKeyCounterRepository.save(apiKeyCounter);

            // Créer un nouvel objet événement resize
            EventResize eventResize = new EventResize(
                    site.getUserId(),
                    eventResizeDTO.getScreenWidth(),
                    eventResizeDTO.getScreenHeight(),
                    eventResizeDTO.getClientTime(),
                    request.getHeader("User-Agent"),
                    request.getRemoteAddr(),
                    LocalDateTime.now(),
                    site
            );

            eventResizes.add(eventResize);
        }

        // Enregistrer les événements de resize
        eventResizeRepository.saveAll(eventResizes);

        return ResponseEntity.status(HttpStatus.CREATED).body(eventResizes);
    }
}


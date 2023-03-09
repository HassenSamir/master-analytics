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
import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class EventServices {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private EventClickRepository eventClickRepository;

    @Autowired
    private EventPageChangeRepository eventPageChangeRepository;

    @Autowired
    private EventResizeRepository eventResizeRepository;


    // GET

    // ADMIN SERVICE
   /* public Page<EventClick> findAllClickEvents() {
        return eventClickRepository.findAll();
    }

    public Page<EventPageChange> findAllPageChangeEvents(Pageable pageable) {
        return eventPageChangeRepository.findAll(pageable);
    }

    public Page<EventResize> findAllResizeEvents() {
        return eventResizeRepository.findAll();
    }*/


    // USER SERVICE

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
    public ResponseEntity<?> createClickEvent(EventClickDTO clickEventDTO, HttpServletRequest request, String apiKey) throws IOException {
        // Vérifier si l'apiKey existe dans la base de données
        // et si l'URL de la requête correspond à l'URL du site
        ResponseEntity<?> checkIfApiKeyMatchSiteInDbAndIfReqUrlIsValid = Utils.checkSiteApiKey(request, apiKey, siteRepository);
        if (checkIfApiKeyMatchSiteInDbAndIfReqUrlIsValid != null) {
            return checkIfApiKeyMatchSiteInDbAndIfReqUrlIsValid;
        }

        Optional<Site> siteOptional = siteRepository.findByApiKey(apiKey);
        Site site = siteOptional.get();



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

        // Créer un nouvel objet événement de clic
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

        // Enregistrer l'événement de clic
        eventClickRepository.save(clickEvent);

        return ResponseEntity.status(HttpStatus.CREATED).body(clickEvent);
    }

    public ResponseEntity<?> createPageChangeEvent(EventPageChangeDTO pageChangeEventDTO, HttpServletRequest request,String apiKey) throws IOException {
        // Vérifier si l'apiKey existe dans la base de données
        // et si l'URL de la requête correspond à l'URL du site
        ResponseEntity<?> checkIfApiKeyMatchSiteInDbAndIfReqUrlIsValid = Utils.checkSiteApiKey(request, apiKey, siteRepository);
        if (checkIfApiKeyMatchSiteInDbAndIfReqUrlIsValid != null) {
            return checkIfApiKeyMatchSiteInDbAndIfReqUrlIsValid;
        }

        Optional<Site> siteOptional = siteRepository.findByApiKey(apiKey);
        Site site = siteOptional.get();

        // Vérifier si Old page est non nul et non vide
        if (ObjectUtils.isEmpty(pageChangeEventDTO.getOldPage())) {
            throw new IOException("Old page is required");
        }
        // Vérifier si New page est non nul et non vide
        if (ObjectUtils.isEmpty(pageChangeEventDTO.getNewPage())) {
            throw new IOException("New page is required");
        }

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

        // Enregistrer l'événement page change
        eventPageChangeRepository.save(pageChangeEvent);

        return ResponseEntity.status(HttpStatus.CREATED).body(pageChangeEvent);
    }

    public ResponseEntity<?> createResizeEvent(EventResizeDTO eventResizeDTO, HttpServletRequest request,String apiKey) throws IOException {
        // Vérifier si l'apiKey existe dans la base de données
        // et si l'URL de la requête correspond à l'URL du site
        ResponseEntity<?> checkIfApiKeyMatchSiteInDbAndIfReqUrlIsValid = Utils.checkSiteApiKey(request, apiKey, siteRepository);
        if (checkIfApiKeyMatchSiteInDbAndIfReqUrlIsValid != null) {
            return checkIfApiKeyMatchSiteInDbAndIfReqUrlIsValid;
        }

        Optional<Site> siteOptional = siteRepository.findByApiKey(apiKey);
        Site site = siteOptional.get();

        // Vérifier si Screen width est non nul et non vide
        if (ObjectUtils.isEmpty(eventResizeDTO.getScreenWidth())) {
            throw new IOException("Screen width is required");
        }
        // Vérifier si Screen height est non nul et non vide
        if (ObjectUtils.isEmpty(eventResizeDTO.getScreenHeight())) {
            throw new IOException("Screen height is required");
        }

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

        // Enregistrer l'événement resize
        eventResizeRepository.save(eventResize);

        return ResponseEntity.status(HttpStatus.CREATED).body(eventResize);
    }

}


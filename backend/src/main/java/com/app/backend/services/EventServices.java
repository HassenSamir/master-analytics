package com.app.backend.services;

import com.app.backend.dto.EventClickDTO;
import com.app.backend.dto.EventPageChangeDTO;
import com.app.backend.dto.EventResizeDTO;
import com.app.backend.handler.ErrorResponse;
import com.app.backend.models.*;
import com.app.backend.payload.response.EventResponse;
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
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
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
    public List<EventClick> findAllClickEvents() {
        return eventClickRepository.findAll();
    }

    public List<EventPageChange> findAllPageChangeEvents() {
        return eventPageChangeRepository.findAll();
    }

    public List<EventResize> findAllResizeEvents() {
        return eventResizeRepository.findAll();
    }


    // USER SERVICE

    public ResponseEntity<?> findAllEventsByTypeAndUserId(String type, String userId) {
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

        EventData data = new EventData();
        EventResponse response;
        if (type.isEmpty()){
            response = new EventResponse("all", data);
            data.setClickEvents(eventClickRepository.findAllByUserId(userId));
            data.setPageChangeEvents(eventPageChangeRepository.findAllByUserId(userId));
            data.setResizeEvents(eventResizeRepository.findAllByUserId(userId));
        } else if (type.equalsIgnoreCase("click")) {
            List<EventClick> clickEvents = eventClickRepository.findAllByUserId(userId);
            data.setClickEvents(clickEvents);
            response = new EventResponse(type, data);
        } else if (type.equalsIgnoreCase("page_change")) {
            List<EventPageChange> pageChangesEvents = eventPageChangeRepository.findAllByUserId(userId);
            data.setPageChangeEvents(pageChangesEvents);
            response = new EventResponse(type, data);
        } else if (type.equalsIgnoreCase("resize")) {
            List<EventResize> resizeEvents = eventResizeRepository.findAllByUserId(userId);
            data.setResizeEvents(resizeEvents);
            response = new EventResponse(type, data);
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", "Invalid event type: " + type));
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    public ResponseEntity<?> findAllEventsByTypeAndSiteId(String type, String siteId) {
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

        EventData data = new EventData();
        EventResponse response;
        if (type.isEmpty()){
            response = new EventResponse("all", data);
            data.setClickEvents(eventClickRepository.findAllBySite(site));
            data.setPageChangeEvents(eventPageChangeRepository.findAllBySite(site));
            data.setResizeEvents(eventResizeRepository.findAllBySite(site));
        } else if (type.equalsIgnoreCase("click")) {
            List<EventClick> clickEvents = eventClickRepository.findAllBySite(site);
            data.setClickEvents(clickEvents);
            response = new EventResponse(type, data);
        } else if (type.equalsIgnoreCase("page_change")) {
            List<EventPageChange> pageChangesEvents = eventPageChangeRepository.findAllBySite(site);
            data.setPageChangeEvents(pageChangesEvents);
            response = new EventResponse(type, data);
        } else if (type.equalsIgnoreCase("resize")) {
            List<EventResize> resizeEvents = eventResizeRepository.findAllBySite(site);
            data.setResizeEvents(resizeEvents);
            response = new EventResponse(type, data);
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
                clickEventDTO.getUserId(),
                clickEventDTO.getClientTime(),
                clickEventDTO.getCssSelector(),
                clickEventDTO.getInnerText(),
                request.getRemoteAddr(),
                request.getHeader("User-Agent"),
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
                pageChangeEventDTO.getUserId(),
                pageChangeEventDTO.getOldPage(),
                pageChangeEventDTO.getNewPage(),
                pageChangeEventDTO.getClientTime(),
                request.getRemoteAddr(),
                request.getHeader("User-Agent"),
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
                eventResizeDTO.getUserId(),
                eventResizeDTO.getScreenWidth(),
                eventResizeDTO.getScreenHeight(),
                eventResizeDTO.getClientTime(),
                request.getRemoteAddr(),
                request.getHeader("User-Agent"),
                LocalDateTime.now(),
                site
        );

        // Enregistrer l'événement resize
        eventResizeRepository.save(eventResize);

        return ResponseEntity.status(HttpStatus.CREATED).body(eventResize);
    }

}


package com.app.backend.services;

import com.app.backend.dto.EventClickDTO;
import com.app.backend.dto.EventPageChangeDTO;
import com.app.backend.dto.EventResizeDTO;
import com.app.backend.handler.ErrorResponse;
import com.app.backend.models.*;
import com.app.backend.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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


    public List<EventClick> findAllClickEvents() {
        return eventClickRepository.findAll();
    }

    public List<EventPageChange> findAllPageChangeEvents() {
        return eventPageChangeRepository.findAll();
    }

    public List<EventResize> findAllResizeEvents() {
        return eventResizeRepository.findAll();
    }

    public List<EventClick> findAllClickEventsByUserId(String userId) {
            return eventClickRepository.findAllByUserId(userId);
    }
    public List<EventPageChange> findAllPageChangeEventsByUserId(String userId) {
        return eventPageChangeRepository.findAllByUserId(userId);
    }
    public List<EventResize> findAllResizeEventsByUserId(String userId) {
        return eventResizeRepository.findAllByUserId(userId);
    }

    public ResponseEntity<?> createClickEvent(EventClickDTO clickEventDTO, HttpServletRequest request, String apiKey) throws IOException {
        Logger logger = LoggerFactory.getLogger(getClass());
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
        logger.info("requestUrl: {}", requestUrl);
        logger.info("siteUrl: {}", siteUrl);
        if (!isSameDomain(siteUrl,requestUrl)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Not Found", "Invalid request URL "+ requestUrl));
        }


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

    public EventPageChange createPageChangeEvent(EventPageChangeDTO pageChangeEventDTO, HttpServletRequest request) throws IOException {
        // Vérifier si l'utilisateur existe
        Optional<User> userOptional = userRepository.findById(pageChangeEventDTO.getUserId());
        if (userOptional.isEmpty()) {
            throw new IOException("User with ID " + pageChangeEventDTO.getUserId() + " not found");
        }

        // Vérifier si Old page est non nul et non vide
        if (StringUtils.isEmpty(pageChangeEventDTO.getOldPage())) {
            throw new IOException("Old page is required");
        }
        // Vérifier si New page est non nul et non vide
        if (StringUtils.isEmpty(pageChangeEventDTO.getNewPage())) {
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
                LocalDateTime.now()
        );

        // Enregistrer l'événement page change
        eventPageChangeRepository.save(pageChangeEvent);

        return pageChangeEvent;
    }

    public EventResize createResizeEvent(EventResizeDTO eventResizeDTO, HttpServletRequest request) throws IOException {
        // Vérifier si l'utilisateur existe
        Optional<User> userOptional = userRepository.findById(eventResizeDTO.getUserId());
        if (userOptional.isEmpty()) {
            throw new IOException("User with ID " + eventResizeDTO.getUserId() + " not found");
        }

        // Vérifier si Screen width est non nul et non vide
        if (StringUtils.isEmpty(eventResizeDTO.getScreenWidth())) {
            throw new IOException("Screen width is required");
        }
        // Vérifier si Screen height est non nul et non vide
        if (StringUtils.isEmpty(eventResizeDTO.getScreenHeight())) {
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
                LocalDateTime.now()
        );

        // Enregistrer l'événement resize
        eventResizeRepository.save(eventResize);

        return eventResize;
    }

    public boolean isSameDomain(String url1, String url2) throws MalformedURLException {
        URL u1 = new URL(url1);
        URL u2 = new URL(url2);

        String host1 = u1.getHost();
        String host2 = u2.getHost();

        String protocol1 = u1.getProtocol();
        String protocol2 = u2.getProtocol();

        return host1.equals(host2) && protocol1.equals(protocol2);
    }

}


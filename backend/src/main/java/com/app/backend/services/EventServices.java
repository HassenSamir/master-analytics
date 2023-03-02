package com.app.backend.services;

import com.app.backend.dto.EventClickDTO;
import com.app.backend.dto.EventPageChangeDTO;
import com.app.backend.dto.EventResizeDTO;
import com.app.backend.models.EventClick;
import com.app.backend.models.EventPageChange;
import com.app.backend.models.EventResize;
import com.app.backend.models.User;
import com.app.backend.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class EventServices {

    @Autowired
    private UserRepository userRepository;
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

    public EventClick createClickEvent(EventClickDTO clickEventDTO, HttpServletRequest request) throws IOException {
        // Vérifier si l'utilisateur existe
        Optional<User> userOptional = userRepository.findById(clickEventDTO.getUserId());
        if (userOptional.isEmpty()) {
            throw new IOException("User with ID " + clickEventDTO.getUserId() + " not found");
        }
        // Vérifier si le sélecteur CSS est non nul et non vide
        if (StringUtils.isEmpty(clickEventDTO.getCssSelector())) {
            throw new IOException("CSS selector is required");
        }
        // Vérifier si le texte interne est non nul et non vide
        if (StringUtils.isEmpty(clickEventDTO.getInnerText())) {
            throw new IOException("Inner text is required");
        }
        // Créer un nouvel objet événement de clic
        EventClick clickEvent = new EventClick(
                clickEventDTO.getUserId(),
                clickEventDTO.getClientTime(),
                clickEventDTO.getCssSelector(),
                clickEventDTO.getInnerText(),
                request.getRemoteAddr(),
                request.getHeader("User-Agent"),
                LocalDateTime.now()
        );

        // Enregistrer l'événement de clic
        eventClickRepository.save(clickEvent);

        return clickEvent;
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

}


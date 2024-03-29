package com.app.backend.services;

import com.app.backend.dto.response.*;
import com.app.backend.handler.ErrorResponse;
import com.app.backend.models.*;
import com.app.backend.repository.*;
import com.app.backend.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventMetricsService {
    private final Logger logger = LoggerFactory.getLogger(EventMetricsService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private EventClickRepository eventClickRepository;

    @Autowired
    private EventResizeRepository eventResizeRepository;

    @Autowired
    private EventPageChangeRepository eventPageChangeRepository;


    public ResponseEntity<?> getEventMetrics(String userId) {
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

        int clickCount = eventClickRepository.countByUserId(userId);

        int resizeCount = eventResizeRepository.countByUserId(userId);

        int pageChangeCount = eventPageChangeRepository.countByUserId(userId);

        /*List<EventCustom> customEvents = eventCustomRepository.findByUser(user);
        int customCount = customEvents.size();*/
        int customCount = 0;

        int totalCount = clickCount + resizeCount + pageChangeCount + customCount;

        EventMetricsDTO response = new EventMetricsDTO(clickCount, resizeCount, pageChangeCount, customCount, totalCount);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    public ResponseEntity<?> getEventMetricsByPeriodAndUserId(String userId, String period, Integer year) {
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

        // Si l'année n'est pas spécifiée, utiliser l'année en cours
        if (year == null) {
            year = LocalDate.now().getYear();
        }

        // Déterminer la plage de dates pour la période demandée
        LocalDateTime startDate;
        LocalDateTime endDate;
        switch (period) {
            // Si la période est "month", la plage de dates couvre toute l'année spécifiée
            case "month" -> {
                YearMonth yearMonth = YearMonth.of(year, Month.JANUARY);
                LocalDate firstDayOfMonth = yearMonth.atDay(1);
                LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();
                startDate = LocalDateTime.of(firstDayOfMonth, LocalTime.MIN);
                endDate = LocalDateTime.of(lastDayOfMonth, LocalTime.MAX);
            }
            // Si la période est "week", la plage de dates couvre la semaine en cours (du lundi au dimanche)
            case "week" -> {
                startDate = LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)), LocalTime.MIDNIGHT);
                endDate = startDate.plusDays(6);
            }
            // Si la période est inconnue, renvoyer une erreur
            default -> {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", "Invalid period: " + period));
            }
        }

        // Compter les événements pour chaque jour ou mois, selon la période demandée
        Map<String, Integer> eventMetrics = new HashMap<>();
        if (period.equals("month")) {
            // Compter les événements pour chaque mois de l'année spécifiée
            for (int month = 1; month <= 12; month++) {
                LocalDateTime monthStartDate = LocalDateTime.of(year, Month.of(month), 1, 0, 0);
                LocalDateTime monthEndDate = LocalDateTime.of(year, Month.of(month), Month.of(month).length(Year.isLeap(year)), 23, 59, 59);
                int eventsClick = eventClickRepository.countByUserIdAndClientTimeBetween(userId, monthStartDate, monthEndDate);
                int eventsResize = eventResizeRepository.countByUserIdAndClientTimeBetween(userId, monthStartDate, monthEndDate);
                int eventsPageChange = eventPageChangeRepository.countByUserIdAndClientTimeBetween(userId, monthStartDate, monthEndDate);
                int eventsCount = eventsClick + eventsResize + eventsPageChange;
                logger.info("monthStartDate {}", monthStartDate);
                logger.info("monthEndDate {}", monthEndDate);
                if (eventsCount > 0) {
                    String monthName = Month.of(month).name();
                    eventMetrics.put(monthName, eventsCount);
                    logger.info("Found events for month {}", monthName);
                }
            }
        } else {
            // Compter les événements pour chaque jour de la semaine en cours
            for (DayOfWeek day : DayOfWeek.values()) {
                LocalDateTime dayStartDate = startDate.with(TemporalAdjusters.nextOrSame(day));
                LocalDateTime dayEndDate = dayStartDate.plusDays(1).minusSeconds(1);
                int eventsClick = eventClickRepository.countByUserIdAndClientTimeBetween(userId, dayStartDate, dayEndDate);
                int eventsResize = eventResizeRepository.countByUserIdAndClientTimeBetween(userId, dayStartDate, dayEndDate);
                int eventsPageChange = eventPageChangeRepository.countByUserIdAndClientTimeBetween(userId, dayStartDate, dayEndDate);
                int eventsCount = eventsClick + eventsResize + eventsPageChange;
                if (eventsCount > 0) {
                    eventMetrics.put(day.getDisplayName(TextStyle.FULL, Locale.ENGLISH), eventsCount);
                }
            }
        }

        // Créer une instance de l'objet EventMetricsPeriodDTO pour stocker les données des événements
        EventMetricsPeriodDTO eventMetricsPeriodDTO = new EventMetricsPeriodDTO();
        // Mettre à jour les informations de période et d'année dans l'objet EventMetricsPeriodDTO
        eventMetricsPeriodDTO.setPeriod(period);
        eventMetricsPeriodDTO.setYear(Integer.valueOf(year.toString()));

        // Créer la liste de données pour chaque label (jour, mois)
        List<EventMetricsDataDTO> data = new ArrayList<>();

        // Si la période est "month", ajouter les données pour chaque mois de l'année
        if (period.equals("month")) {
            for (Month month : Month.values()) {
                if (eventMetrics.containsKey(month.name())) {
                    data.add(new EventMetricsDataDTO(month.getDisplayName(TextStyle.FULL, Locale.ENGLISH), eventMetrics.get(month.name())));
                }
            }
        } else {
            // Si la période est "week", ajouter les données pour chaque jour de la semaine en cours
            for (DayOfWeek day : DayOfWeek.values()) {
                if (eventMetrics.containsKey(day.getDisplayName(TextStyle.FULL, Locale.ENGLISH))) {
                    data.add(new EventMetricsDataDTO(day.getDisplayName(TextStyle.FULL, Locale.ENGLISH), eventMetrics.get(day.getDisplayName(TextStyle.FULL, Locale.ENGLISH))));
                }
            }
        }

        // Ajouter les données de réponse à l'objet EventMetricsPeriodDTO
        eventMetricsPeriodDTO.setData(data);

        // Retourner la réponse avec un code d'état OK
        return ResponseEntity.status(HttpStatus.OK).body(eventMetricsPeriodDTO);
    }

    public ResponseEntity<?> getEventMetricsByUserIdAndSiteId(String userId, String siteId) {
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

        Optional<Site> siteOptional = siteRepository.findByIdAndUserId(siteId, userId);
        if (siteOptional.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Not Found", "Site not found or unauthorized"));
        }
        Site site = siteOptional.get();

        // Récupérer le nombre d'événements de chaque type pour le site spécifié
        int clickCount = eventClickRepository.countBySiteId(siteId);
        int resizeCount = eventResizeRepository.countBySiteId(siteId);
        int pageChangeCount = eventPageChangeRepository.countBySiteId(siteId);
        int customCount = 0;

        // Calculer le total d'événements
        int total = clickCount + resizeCount + pageChangeCount;

        // Formater les données en un objet SiteEventMetricsDTO
        SiteEventMetricsDTO eventMetrics = new SiteEventMetricsDTO(site.getName(), clickCount, resizeCount, pageChangeCount, customCount, total);

        // Retourner la réponse avec un code d'état OK
        return ResponseEntity.status(HttpStatus.OK).body(eventMetrics);
    }


    public ResponseEntity<?> getLastEventsMetricsByTypeAndUserId(String userId, String type, int page, int size) {
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

        // Vérifier que le type d'événement est valide
        if (!Arrays.asList("click", "page_change", "resize").contains(type)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", "Invalid type parameter"));
        }

        // Vérifier que la taille de la page ne dépasse pas 50 événements
        if (size > 50) {
            size = 50;
        }
        Pageable pageable = PageRequest.of(page, size);

        Page<? extends EventI> events;

        int totalElements;
        // Sélectionner le type d'événement correspondant au paramètre type
        switch (type) {
            case "click" -> {
                events = eventClickRepository.findAllByUserIdOrderByClientTimeDesc(userId, pageable);
                totalElements = eventClickRepository.countByUserId(userId);
            }
            case "page_change" -> {
                events = eventPageChangeRepository.findAllByUserIdOrderByClientTimeDesc(userId, pageable);
                totalElements = eventPageChangeRepository.countByUserId(userId);
            }
            case "resize" -> {
                events = eventResizeRepository.findAllByUserIdOrderByClientTimeDesc(userId, pageable);
                totalElements = eventResizeRepository.countByUserId(userId);

            }
            default -> {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", "Invalid type parameter"));
            }
        }


        // Calculer le nombre maximal d'événements que l'on peut afficher sur la page en fonction du nombre d'événements disponibles pour chaque type
        int maxEventsPerPage = Math.min(size, (int) (events.getTotalElements() - pageable.getOffset()));


        // Trier la liste d'événements par date décroissante
        List<EventI> sortedEvents = events.getContent()
                .stream()
                .sorted(Comparator.comparing(EventI::getClientTime, LocalDateTime::compareTo).reversed())
                .collect(Collectors.toList());

        logger.info("events size {}", sortedEvents.size());
        // Paginer la liste d'événements
        int start = (int)pageable.getOffset();
        int end = Math.min((start + maxEventsPerPage), sortedEvents.size());
        logger.info("events start {}", start);
        logger.info("events end {}", end);

        List<EventI> pagedEvents = sortedEvents.subList(0, end);
        logger.info("pagedEvents {}", pagedEvents.size());
        int totalPages = (int) Math.ceil((double) totalElements / size);

        EventResponseDTO response = new EventResponseDTO("latest", pagedEvents, pageable,totalElements,totalPages);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}

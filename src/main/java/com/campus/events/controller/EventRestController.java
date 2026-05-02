package com.campus.events.controller;

import com.campus.events.model.Event;
import com.campus.events.model.Registration;
import com.campus.events.service.EventService;
import com.campus.events.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST API endpoints (used by AJAX or external clients)
 * Base URL: /api/
 */
@RestController
@RequestMapping("/api")
public class EventRestController {

    @Autowired
    private EventService eventService;

    @Autowired
    private RegistrationService registrationService;

    // GET /api/events  → all upcoming events as JSON
    @GetMapping("/events")
    public List<Event> getAllEvents() {
        return eventService.getUpcomingEvents();
    }

    // GET /api/events/{id}
    @GetMapping("/events/{id}")
    public ResponseEntity<Event> getEvent(@PathVariable Long id) {
        return eventService.getEventById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/events/{id}/stats
    @GetMapping("/events/{id}/stats")
    public ResponseEntity<Map<String, Object>> getEventStats(@PathVariable Long id) {
        return eventService.getEventById(id).map(event -> {
            long regCount = registrationService.countByEvent(id);

            Map<String, Object> response = new java.util.HashMap<>();
            response.put("eventId", id);
            response.put("title", event.getTitle());
            response.put("capacity", event.getCapacity());
            response.put("registered", regCount);
            response.put("available", event.getCapacity() - regCount);

            return ResponseEntity.ok(response);
        }).orElse(ResponseEntity.notFound().build());
    }

    // GET /api/registrations/event/{eventId}
    @GetMapping("/registrations/event/{eventId}")
    public List<Registration> getRegistrationsByEvent(@PathVariable Long eventId) {
        return registrationService.getRegistrationsByEvent(eventId);
    }

    // GET /api/stats/by-type
    @GetMapping("/stats/by-type")
    public List<Object[]> statsByType() {
        return eventService.getRegistrationStatsByType();
    }
}

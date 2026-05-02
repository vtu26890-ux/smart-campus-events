package com.campus.events.service;

import com.campus.events.model.Event;
import com.campus.events.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getUpcomingEvents() {
        return eventRepository.findByEventDateGreaterThanEqualOrderByEventDateAsc(LocalDate.now());
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public List<Event> searchEvents(String dept, String type, LocalDate fromDate, LocalDate toDate) {
        return eventRepository.searchEvents(dept, type, fromDate, toDate);
    }

    public List<Event> searchByTitle(String keyword) {
        return eventRepository.findByTitleContainingIgnoreCase(keyword);
    }

    public List<Object[]> getRegistrationStatsByType() {
        return eventRepository.countRegistrationsByType();
    }

    public boolean hasSeatsAvailable(Event event) {
        return event.getRegisteredCount() < event.getCapacity();
    }

    public void incrementRegistrationCount(Long eventId) {
        eventRepository.findById(eventId).ifPresent(event -> {
            event.setRegisteredCount(event.getRegisteredCount() + 1);
            eventRepository.save(event);
        });
    }
}
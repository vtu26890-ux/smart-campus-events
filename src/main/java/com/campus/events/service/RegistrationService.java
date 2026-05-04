package com.campus.events.service;

import com.campus.events.model.Event;
import com.campus.events.model.Registration;
import com.campus.events.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private EventService eventService;

    @Autowired
    private EmailService emailService;

    public Registration register(Registration registration) {
        if (registrationRepository.existsByEmailAndEventId(
                registration.getEmail(), registration.getEvent().getId())) {
            throw new IllegalStateException("You are already registered for this event.");
        }

        Event event = registration.getEvent();
        if (!eventService.hasSeatsAvailable(event)) {
            throw new IllegalStateException("Event is fully booked.");
        }

        registration.setRegisteredAt(LocalDateTime.now());
        Registration saved = registrationRepository.save(registration);
        eventService.incrementRegistrationCount(event.getId());
        emailService.sendRegistrationConfirmation(saved);
        return saved;
    }

    public void cancelRegistration(Long registrationId, String email) {
        Registration reg = registrationRepository.findById(registrationId)
            .orElseThrow(() -> new RuntimeException("Registration not found"));

        // Security check — only the owner can cancel
        if (!reg.getEmail().equalsIgnoreCase(email)) {
            throw new RuntimeException("Unauthorized: You can only cancel your own registrations");
        }

        // Decrease event registration count
        eventService.decrementRegistrationCount(reg.getEvent().getId());

        registrationRepository.deleteById(registrationId);
    }

    // Fixed: uses JOIN FETCH query to eagerly load event within the same Hibernate session,
    // preventing LazyInitializationException when Thymeleaf accesses registration.event fields
    public List<Registration> getRegistrationsByEmail(String email) {
        return registrationRepository.findByEmailWithEvent(email);
    }

    public List<Registration> getRegistrationsByEvent(Long eventId) {
        return registrationRepository.findByEventId(eventId);
    }

    public List<Registration> getAllRegistrations() {
        return registrationRepository.findAll();
    }

    public Optional<Registration> getById(Long id) {
        return registrationRepository.findById(id);
    }

    public Registration saveFeedback(Long id, int rating, String comment) {
        Registration reg = registrationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Registration not found"));
        reg.setRating(rating);
        reg.setFeedbackComment(comment);
        return registrationRepository.save(reg);
    }

    public List<Object[]> getAvgRatings() {
        return registrationRepository.avgRatingPerEvent();
    }

    public long countByEvent(Long eventId) {
        return registrationRepository.countByEventId(eventId);
    }
}

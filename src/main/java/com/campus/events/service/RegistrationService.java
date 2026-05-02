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
        // Check duplicate
        if (registrationRepository.existsByEmailAndEventId(
                registration.getEmail(), registration.getEvent().getId())) {
            throw new IllegalStateException("You are already registered for this event.");
        }

        // Check capacity
        Event event = registration.getEvent();
        if (!eventService.hasSeatsAvailable(event)) {
            throw new IllegalStateException("Event is fully booked.");
        }

        registration.setRegisteredAt(LocalDateTime.now());
        Registration saved = registrationRepository.save(registration);
        eventService.incrementRegistrationCount(event.getId());

        // Send confirmation email asynchronously (non-blocking)
        emailService.sendRegistrationConfirmation(saved);

        return saved;
    }

    public List<Registration> getRegistrationsByEmail(String email) {
        return registrationRepository.findByEmail(email);
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

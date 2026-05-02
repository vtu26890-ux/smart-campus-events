package com.campus.events.controller;

import com.campus.events.model.Event;
import com.campus.events.model.Registration;
import com.campus.events.service.EventService;
import com.campus.events.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class StudentController {

    @Autowired
    private EventService eventService;

    @Autowired
    private RegistrationService registrationService;

    // Home page
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("upcomingEvents", eventService.getUpcomingEvents()
            .stream().limit(3).toList());
        return "index";
    }

    // Browse all upcoming events
    @GetMapping("/events")
    public String browseEvents(Model model) {
        model.addAttribute("events", eventService.getUpcomingEvents());
        return "events";
    }

    // View single event detail
    @GetMapping("/events/{id}")
    public String eventDetail(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id)
            .orElseThrow(() -> new RuntimeException("Event not found: " + id));
        model.addAttribute("event", event);
        model.addAttribute("registration", new Registration());
        return "event-detail";
    }

    // Show registration form
    @GetMapping("/register/{eventId}")
    public String showRegisterForm(@PathVariable Long eventId, Model model) {
        Event event = eventService.getEventById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        model.addAttribute("event", event);
        model.addAttribute("registration", new Registration());
        return "register-event";
    }

    // Submit registration
    @PostMapping("/register/{eventId}")
    public String submitRegistration(@PathVariable Long eventId,
                                     @Valid @ModelAttribute("registration") Registration registration,
                                     BindingResult result,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        Event event = eventService.getEventById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));

        if (result.hasErrors()) {
            model.addAttribute("event", event);
            return "register-event";
        }

        registration.setEvent(event);
        try {
            registrationService.register(registration);
            redirectAttributes.addFlashAttribute("success",
                "Successfully registered for: " + event.getTitle());
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/events";
    }

    // View my registrations by email
    @GetMapping("/my-registrations")
    public String myRegistrations(@RequestParam(required = false) String email, Model model) {
        if (email != null && !email.isBlank()) {
            List<Registration> regs = registrationService.getRegistrationsByEmail(email);
            model.addAttribute("registrations", regs);
            model.addAttribute("email", email);
        }
        return "my-registrations";
    }

    // Login page
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}

package com.campus.events.controller;

import com.campus.events.model.Event;
import com.campus.events.model.Registration;
import com.campus.events.service.EventService;
import com.campus.events.service.RegistrationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
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

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("upcomingEvents", eventService.getUpcomingEvents()
            .stream().limit(3).toList());
        return "index";
    }

    @GetMapping("/events")
    public String browseEvents(Model model) {
        model.addAttribute("events", eventService.getUpcomingEvents());
        return "events";
    }

    @GetMapping("/events/{id}")
    public String eventDetail(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id)
            .orElseThrow(() -> new RuntimeException("Event not found: " + id));
        model.addAttribute("event", event);
        model.addAttribute("registration", new Registration());
        return "event-detail";
    }

    // OAuth2 success — save Gmail to session
    @GetMapping("/oauth2/success")
    public String oauth2Success(@AuthenticationPrincipal OAuth2User principal,
                                HttpSession session) {
        if (principal != null) {
            session.setAttribute("oauth2Email", principal.getAttribute("email"));
            session.setAttribute("oauth2Name",  principal.getAttribute("name"));
        }
        return "redirect:/events";
    }

    // Registration — only accessible when logged in (enforced by SecurityConfig)
    @GetMapping("/register/{eventId}")
    public String showRegisterForm(@PathVariable Long eventId,
                                   Model model,
                                   HttpSession session,
                                   @AuthenticationPrincipal OAuth2User principal) {
        Event event = eventService.getEventById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));

        Registration registration = new Registration();

        // Pre-fill from Google session
        String email = (String) session.getAttribute("oauth2Email");
        String name  = (String) session.getAttribute("oauth2Name");

        // Also try directly from principal if session not set yet
        if (email == null && principal != null) {
            email = principal.getAttribute("email");
            name  = principal.getAttribute("name");
            session.setAttribute("oauth2Email", email);
            session.setAttribute("oauth2Name",  name);
        }

        if (email != null) registration.setEmail(email);
        if (name  != null) registration.setStudentName(name);

        model.addAttribute("event", event);
        model.addAttribute("registration", registration);
        model.addAttribute("googleEmail", email);
        return "register-event";
    }

    @PostMapping("/register/{eventId}")
    public String submitRegistration(@PathVariable Long eventId,
                                     @Valid @ModelAttribute("registration") Registration registration,
                                     BindingResult result,
                                     Model model,
                                     HttpSession session,
                                     @AuthenticationPrincipal OAuth2User principal,
                                     RedirectAttributes redirectAttributes) {
        // Security: must be logged in via Google OAuth2 to register
        if (principal == null) {
            return "redirect:/login";
        }

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
                "🎉 Successfully registered for: " + event.getTitle() +
                ". Check your email for confirmation!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/events";
    }

    @GetMapping("/my-registrations")
    public String myRegistrations(@RequestParam(required = false) String email,
                                  Model model,
                                  HttpSession session) {
        // Auto-fill email from Google session
        if (email == null || email.isBlank()) {
            email = (String) session.getAttribute("oauth2Email");
        }
        if (email != null && !email.isBlank()) {
            List<Registration> regs = registrationService.getRegistrationsByEmail(email);
            model.addAttribute("registrations", regs);
            model.addAttribute("email", email);
        }
        return "my-registrations";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    @PostMapping("/my-registrations/cancel/{id}")
    public String cancelRegistration(@PathVariable Long id,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        String email = (String) session.getAttribute("oauth2Email");

        if (email == null || email.isBlank()) {
            return "redirect:/login";
        }

        try {
            registrationService.cancelRegistration(id, email);
            redirectAttributes.addFlashAttribute("success", "✅ Registration cancelled successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ " + e.getMessage());
        }

        return "redirect:/my-registrations";
}
}
package com.campus.events.controller;

import com.campus.events.model.Event;
import com.campus.events.service.EventService;
import com.campus.events.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private EventService eventService;

    @Autowired
    private RegistrationService registrationService;

    // Admin Dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalEvents", eventService.getAllEvents().size());
        model.addAttribute("totalRegistrations", registrationService.getAllRegistrations().size());
        model.addAttribute("recentEvents", eventService.getAllEvents()
            .stream().limit(5).toList());
        model.addAttribute("statsByType", eventService.getRegistrationStatsByType());
        model.addAttribute("avgRatings", registrationService.getAvgRatings());
        return "admin/dashboard";
    }

    // List all events with search/filter
    @GetMapping("/events")
    public String listEvents(
            @RequestParam(required = false) String dept,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String keyword,
            Model model) {

        List<Event> events;
        if (keyword != null && !keyword.isBlank()) {
            events = eventService.searchByTitle(keyword);
        } else if (dept != null || type != null || fromDate != null || toDate != null) {
            events = eventService.searchEvents(dept, type, fromDate, toDate);
        } else {
            events = eventService.getAllEvents();
        }

        model.addAttribute("events", events);
        model.addAttribute("dept", dept);
        model.addAttribute("type", type);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("keyword", keyword);
        return "admin/events-list";
    }

    // Show Add Event form
    @GetMapping("/events/add")
    public String showAddForm(Model model) {
        model.addAttribute("event", new Event());
        return "admin/add-event";
    }

    // Save new event
    @PostMapping("/events/add")
    public String saveEvent(@Valid @ModelAttribute("event") Event event,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/add-event";
        }
        eventService.saveEvent(event);
        redirectAttributes.addFlashAttribute("success", "Event created successfully!");
        return "redirect:/admin/events";
    }

    // Show Edit form
    @GetMapping("/events/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        model.addAttribute("event", event);
        return "admin/edit-event";
    }

    // Update event
    @PostMapping("/events/edit/{id}")
    public String updateEvent(@PathVariable Long id,
                              @Valid @ModelAttribute("event") Event event,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/edit-event";
        }
        event.setId(id);
        eventService.saveEvent(event);
        redirectAttributes.addFlashAttribute("success", "Event updated successfully!");
        return "redirect:/admin/events";
    }

    // Delete event
    @PostMapping("/events/delete/{id}")
    public String deleteEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        eventService.deleteEvent(id);
        redirectAttributes.addFlashAttribute("success", "Event deleted.");
        return "redirect:/admin/events";
    }

    // View registrations for an event
    @GetMapping("/events/{id}/registrations")
    public String viewRegistrations(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        model.addAttribute("event", event);
        model.addAttribute("registrations", registrationService.getRegistrationsByEvent(id));
        model.addAttribute("count", registrationService.countByEvent(id));
        return "admin/registrations";
    }
}

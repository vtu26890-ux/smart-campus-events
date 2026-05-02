package com.campus.events.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    @Column(nullable = false)
    private String title;

    @NotNull(message = "Description is required")
    @Size(min = 10, message = "Description must be at least 10 characters")
    @Column(length = 1000)
    private String description;

    @NotNull(message = "Event date is required")
    private LocalDate eventDate;

    private LocalTime eventTime;

    @NotNull(message = "Venue is required")
    private String venue;

    @NotNull(message = "Department is required")
    private String department;

    // e.g. Workshop, Seminar, Cultural, Technical, Sports
    @NotNull(message = "Event type is required")
    private String eventType;

    @Min(value = 1, message = "Capacity must be at least 1")
    private int capacity;

    private int registeredCount = 0;

    private String organizer;
}

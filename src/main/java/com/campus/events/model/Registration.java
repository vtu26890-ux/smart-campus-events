package com.campus.events.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "registrations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Student name is required")
    @Size(min = 2, max = 80, message = "Name must be 2–80 characters")
    private String studentName;

    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Roll number is required")
    @Size(min = 3, max = 20)
    private String rollNumber;

    @NotNull(message = "Department is required")
    private String department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    private LocalDateTime registeredAt = LocalDateTime.now();

    // Feedback fields (optional, filled after event)
    private Integer rating;        // 1–5
    private String feedbackComment;
}

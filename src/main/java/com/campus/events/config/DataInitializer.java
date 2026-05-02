package com.campus.events.config;

import com.campus.events.model.Event;
import com.campus.events.repository.EventRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalTime;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedData(EventRepository eventRepository) {
        return args -> {
            if (eventRepository.count() == 0) {
                eventRepository.save(Event.builder()
                    .title("National Hackathon 2025")
                    .description("A 24-hour hackathon where students build innovative solutions for real-world problems. Open to all branches. Prizes worth ₹50,000!")
                    .eventDate(LocalDate.now().plusDays(10))
                    .eventTime(LocalTime.of(9, 0))
                    .venue("Innovation Lab, Block C")
                    .department("CSE")
                    .eventType("Technical")
                    .capacity(100)
                    .organizer("CSE Department")
                    .build());

                eventRepository.save(Event.builder()
                    .title("Spring Fest Cultural Night")
                    .description("Annual cultural extravaganza featuring music, dance, drama, and art performances by student clubs.")
                    .eventDate(LocalDate.now().plusDays(5))
                    .eventTime(LocalTime.of(18, 0))
                    .venue("Open Air Amphitheatre")
                    .department("All")
                    .eventType("Cultural")
                    .capacity(500)
                    .organizer("Student Council")
                    .build());

                eventRepository.save(Event.builder()
                    .title("Machine Learning Workshop")
                    .description("Hands-on workshop on ML fundamentals, supervised learning, and neural networks using Python & TensorFlow.")
                    .eventDate(LocalDate.now().plusDays(15))
                    .eventTime(LocalTime.of(10, 0))
                    .venue("Seminar Hall A")
                    .department("CSE")
                    .eventType("Workshop")
                    .capacity(60)
                    .organizer("AI Club")
                    .build());

                eventRepository.save(Event.builder()
                    .title("Industry 4.0 Seminar")
                    .description("Expert seminar on IoT, automation, and smart manufacturing trends shaping the future of engineering.")
                    .eventDate(LocalDate.now().plusDays(8))
                    .eventTime(LocalTime.of(11, 0))
                    .venue("Auditorium")
                    .department("MECH")
                    .eventType("Seminar")
                    .capacity(200)
                    .organizer("Mechanical Department")
                    .build());

                eventRepository.save(Event.builder()
                    .title("Inter-College Cricket Tournament")
                    .description("Three-day cricket tournament featuring 8 college teams competing for the championship cup.")
                    .eventDate(LocalDate.now().plusDays(20))
                    .eventTime(LocalTime.of(8, 0))
                    .venue("College Cricket Ground")
                    .department("All")
                    .eventType("Sports")
                    .capacity(300)
                    .organizer("Sports Committee")
                    .build());

                System.out.println("✅ Sample events loaded successfully.");
            }
        };
    }
}

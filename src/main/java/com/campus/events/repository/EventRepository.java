package com.campus.events.repository;

import com.campus.events.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Filter by department
    List<Event> findByDepartmentIgnoreCase(String department);

    // Filter by event type
    List<Event> findByEventTypeIgnoreCase(String eventType);

    // Filter by date range
    List<Event> findByEventDateBetween(LocalDate start, LocalDate end);

    // Filter by date (upcoming)
    List<Event> findByEventDateGreaterThanEqualOrderByEventDateAsc(LocalDate today);

    // Combined search
    @Query("SELECT e FROM Event e WHERE " +
           "(:dept IS NULL OR LOWER(e.department) = LOWER(:dept)) AND " +
           "(:type IS NULL OR LOWER(e.eventType) = LOWER(:type)) AND " +
           "(:fromDate IS NULL OR e.eventDate >= :fromDate) AND " +
           "(:toDate IS NULL OR e.eventDate <= :toDate)")
    List<Event> searchEvents(
        @Param("dept") String dept,
        @Param("type") String type,
        @Param("fromDate") LocalDate fromDate,
        @Param("toDate") LocalDate toDate
    );

    // Aggregate: count registrations by event type
    @Query("SELECT e.eventType, COUNT(r) FROM Event e LEFT JOIN Registration r ON r.event = e GROUP BY e.eventType")
    List<Object[]> countRegistrationsByType();

    // Title search
    List<Event> findByTitleContainingIgnoreCase(String keyword);
}

package com.campus.events.repository;

import com.campus.events.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    List<Registration> findByEventId(Long eventId);

    // Fixed: JOIN FETCH ensures event data is loaded within the same session,
    // preventing LazyInitializationException on /my-registrations
    @Query("SELECT r FROM Registration r JOIN FETCH r.event WHERE r.email = :email")
    List<Registration> findByEmailWithEvent(@Param("email") String email);

    List<Registration> findByEmail(String email);

    List<Registration> findByRollNumber(String rollNumber);

    boolean existsByEmailAndEventId(String email, Long eventId);

    // Count registrations per event
    @Query("SELECT r.event.id, COUNT(r) FROM Registration r GROUP BY r.event.id")
    List<Object[]> countPerEvent();

    // Average rating per event
    @Query("SELECT r.event.title, AVG(r.rating) FROM Registration r WHERE r.rating IS NOT NULL GROUP BY r.event.title")
    List<Object[]> avgRatingPerEvent();

    // Total registrations for an event
    @Query("SELECT COUNT(r) FROM Registration r WHERE r.event.id = :eventId")
    long countByEventId(@Param("eventId") Long eventId);
}

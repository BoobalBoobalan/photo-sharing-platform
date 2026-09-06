package com.photoshare.platform.repository;

import com.photoshare.platform.entity.Event;
import com.photoshare.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByCreatedBy(User admin);

    @Query("SELECT e FROM Event e JOIN e.assignedTeam u WHERE u.id = :userId")
    List<Event> findEventsAssignedToUser(@Param("userId") Long userId);
}

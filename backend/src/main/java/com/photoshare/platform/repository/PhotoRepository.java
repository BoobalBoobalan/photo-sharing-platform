package com.photoshare.platform.repository;

import com.photoshare.platform.entity.Event;
import com.photoshare.platform.entity.Photo;
import com.photoshare.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    List<Photo> findByEvent(Event event);

    List<Photo> findByEventAndUploadedBy(Event event, User user);

    List<Photo> findByEventAndIsSelectedTrue(Event event);

    long countByEvent(Event event);

    long countByEventAndIsSelectedTrue(Event event);
}

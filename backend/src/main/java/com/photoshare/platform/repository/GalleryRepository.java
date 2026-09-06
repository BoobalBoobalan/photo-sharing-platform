package com.photoshare.platform.repository;

import com.photoshare.platform.entity.Event;
import com.photoshare.platform.entity.Gallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GalleryRepository extends JpaRepository<Gallery, Long> {

    Optional<Gallery> findByEvent(Event event);

    Optional<Gallery> findByGalleryToken(String galleryToken);

    Boolean existsByGalleryToken(String galleryToken);
}

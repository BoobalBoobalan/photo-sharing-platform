package com.photoshare.platform.service;

import com.photoshare.platform.dto.request.PublishGalleryRequest;
import com.photoshare.platform.dto.response.GalleryDto;
import com.photoshare.platform.dto.response.PhotoDto;
import com.photoshare.platform.dto.response.PublicGalleryResponse;
import com.photoshare.platform.entity.Event;
import com.photoshare.platform.entity.Gallery;
import com.photoshare.platform.entity.Photo;
import com.photoshare.platform.entity.Role;
import com.photoshare.platform.entity.User;
import com.photoshare.platform.exception.BadRequestException;
import com.photoshare.platform.exception.ResourceNotFoundException;
import com.photoshare.platform.exception.UnauthorizedAccessException;
import com.photoshare.platform.repository.EventRepository;
import com.photoshare.platform.repository.GalleryRepository;
import com.photoshare.platform.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GalleryService {

    private final GalleryRepository galleryRepository;
    private final EventRepository eventRepository;
    private final PhotoRepository photoRepository;

    @Transactional
    public GalleryDto publishGallery(Long eventId, PublishGalleryRequest request, User admin) {
        if (admin.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedAccessException("Only Admins can publish galleries");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));

        // Mark selected photos if provided
        if (request.getSelectedPhotoIds() != null && !request.getSelectedPhotoIds().isEmpty()) {
            List<Photo> eventPhotos = photoRepository.findByEvent(event);
            for (Photo photo : eventPhotos) {
                photo.setIsSelected(request.getSelectedPhotoIds().contains(photo.getId()));
            }
            photoRepository.saveAll(eventPhotos);
        }

        long selectedCount = photoRepository.countByEventAndIsSelectedTrue(event);
        if (selectedCount == 0) {
            throw new BadRequestException("Cannot publish gallery without selecting at least one photo.");
        }

        Gallery gallery = galleryRepository.findByEvent(event).orElseGet(() ->
            Gallery.builder()
                    .event(event)
                    .galleryToken(generateUniqueToken())
                    .build()
        );

        gallery.setPin(request.getPin());
        gallery.setTitle(request.getTitle() != null ? request.getTitle() : event.getTitle());
        gallery.setIsPublished(true);

        Gallery saved = galleryRepository.save(gallery);
        return GalleryDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public PublicGalleryResponse getPublicGalleryInfo(String galleryToken) {
        Gallery gallery = galleryRepository.findByGalleryToken(galleryToken)
                .orElseThrow(() -> new ResourceNotFoundException("Gallery not found or invalid link"));

        if (!gallery.getIsPublished()) {
            throw new ResourceNotFoundException("This gallery has not been published yet");
        }

        List<Photo> selectedPhotos = photoRepository.findByEventAndIsSelectedTrue(gallery.getEvent());

        return PublicGalleryResponse.builder()
                .galleryToken(gallery.getGalleryToken())
                .eventTitle(gallery.getTitle() != null ? gallery.getTitle() : gallery.getEvent().getTitle())
                .eventDescription(gallery.getEvent().getDescription())
                .eventDate(gallery.getEvent().getEventDate())
                .isPinRequired(true)
                .isPinVerified(false)
                .totalPhotosCount(selectedPhotos.size())
                .photos(null) // Photos are only returned after PIN verification
                .build();
    }

    @Transactional(readOnly = true)
    public PublicGalleryResponse verifyPinAndGetPhotos(String galleryToken, String pin) {
        Gallery gallery = galleryRepository.findByGalleryToken(galleryToken)
                .orElseThrow(() -> new ResourceNotFoundException("Gallery not found or invalid link"));

        if (!gallery.getIsPublished()) {
            throw new ResourceNotFoundException("This gallery has not been published yet");
        }

        if (!gallery.getPin().equals(pin)) {
            throw new BadRequestException("Incorrect PIN. Please try again.");
        }

        List<PhotoDto> photoDtos = photoRepository.findByEventAndIsSelectedTrue(gallery.getEvent()).stream()
                .map(PhotoDto::fromEntity)
                .collect(Collectors.toList());

        return PublicGalleryResponse.builder()
                .galleryToken(gallery.getGalleryToken())
                .eventTitle(gallery.getTitle() != null ? gallery.getTitle() : gallery.getEvent().getTitle())
                .eventDescription(gallery.getEvent().getDescription())
                .eventDate(gallery.getEvent().getEventDate())
                .isPinRequired(true)
                .isPinVerified(true)
                .totalPhotosCount(photoDtos.size())
                .photos(photoDtos)
                .build();
    }

    private String generateUniqueToken() {
        String token = UUID.randomUUID().toString().substring(0, 8);
        while (galleryRepository.existsByGalleryToken(token)) {
            token = UUID.randomUUID().toString().substring(0, 8);
        }
        return token;
    }
}

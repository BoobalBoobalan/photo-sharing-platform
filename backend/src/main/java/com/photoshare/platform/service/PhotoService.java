package com.photoshare.platform.service;

import com.photoshare.platform.dto.response.PhotoDto;
import com.photoshare.platform.entity.Event;
import com.photoshare.platform.entity.Photo;
import com.photoshare.platform.entity.Role;
import com.photoshare.platform.entity.User;
import com.photoshare.platform.exception.BadRequestException;
import com.photoshare.platform.exception.ResourceNotFoundException;
import com.photoshare.platform.exception.UnauthorizedAccessException;
import com.photoshare.platform.repository.EventRepository;
import com.photoshare.platform.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final EventRepository eventRepository;
    private final StorageService storageService;
    private final EventService eventService;

    @Transactional
    public List<PhotoDto> uploadPhotos(Long eventId, List<MultipartFile> files, User currentUser) {
        if (files == null || files.isEmpty()) {
            throw new BadRequestException("No files provided for upload");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));

        eventService.validateUserAccessToEvent(event, currentUser);

        List<PhotoDto> uploadedPhotos = new ArrayList<>();
        String subFolder = "events/" + eventId;

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            try {
                String storageLocation = storageService.storeFile(file, subFolder);

                Photo photo = Photo.builder()
                        .event(event)
                        .uploadedBy(currentUser)
                        .filename(file.getOriginalFilename())
                        .storageLocation(storageLocation)
                        .fileSize(file.getSize())
                        .contentType(file.getContentType())
                        .isSelected(false)
                        .build();

                Photo savedPhoto = photoRepository.save(photo);
                uploadedPhotos.add(PhotoDto.fromEntity(savedPhoto));
            } catch (Exception e) {
                log.error("Failed to upload photo: {}", file.getOriginalFilename(), e);
                throw new BadRequestException("Failed to upload photo: " + file.getOriginalFilename() + ". Error: " + e.getMessage());
            }
        }

        return uploadedPhotos;
    }

    @Transactional(readOnly = true)
    public List<PhotoDto> getPhotosByEvent(Long eventId, User currentUser) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));

        eventService.validateUserAccessToEvent(event, currentUser);

        List<Photo> photos;
        if (currentUser.getRole() == Role.ROLE_ADMIN) {
            photos = photoRepository.findByEvent(event);
        } else {
            photos = photoRepository.findByEventAndUploadedBy(event, currentUser);
        }

        return photos.stream()
                .map(PhotoDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PhotoDto> getAllEventPhotosForAdmin(Long eventId, User currentUser) {
        if (currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedAccessException("Only Admins can view all team uploaded photos");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));

        return photoRepository.findByEvent(event).stream()
                .map(PhotoDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public PhotoDto togglePhotoSelection(Long photoId, Boolean isSelected, User currentUser) {
        if (currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedAccessException("Only Admins can select photos for publishing");
        }

        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found with ID: " + photoId));

        photo.setIsSelected(isSelected);
        Photo updated = photoRepository.save(photo);
        return PhotoDto.fromEntity(updated);
    }

    @Transactional
    public void deletePhoto(Long photoId, User currentUser) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found with ID: " + photoId));

        if (currentUser.getRole() != Role.ROLE_ADMIN && !photo.getUploadedBy().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You cannot delete another user's photo");
        }

        storageService.deleteFile(photo.getStorageLocation());
        photoRepository.delete(photo);
    }
}

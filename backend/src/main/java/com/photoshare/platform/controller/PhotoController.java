package com.photoshare.platform.controller;

import com.photoshare.platform.dto.response.ApiResponse;
import com.photoshare.platform.dto.response.PhotoDto;
import com.photoshare.platform.entity.User;
import com.photoshare.platform.security.SecurityUtils;
import com.photoshare.platform.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;
    private final SecurityUtils securityUtils;

    @PostMapping("/events/{eventId}/photos/upload")
    public ResponseEntity<ApiResponse<List<PhotoDto>>> uploadPhotos(
            @PathVariable Long eventId,
            @RequestParam("files") List<MultipartFile> files) {
        User currentUser = securityUtils.getCurrentUser();
        List<PhotoDto> uploaded = photoService.uploadPhotos(eventId, files, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Photos uploaded successfully", uploaded));
    }

    @GetMapping("/events/{eventId}/photos")
    public ResponseEntity<ApiResponse<List<PhotoDto>>> getPhotos(@PathVariable Long eventId) {
        User currentUser = securityUtils.getCurrentUser();
        List<PhotoDto> photos = photoService.getPhotosByEvent(eventId, currentUser);
        return ResponseEntity.ok(ApiResponse.success(photos));
    }

    @GetMapping("/admin/events/{eventId}/photos")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<PhotoDto>>> getAllTeamPhotosForAdmin(@PathVariable Long eventId) {
        User currentUser = securityUtils.getCurrentUser();
        List<PhotoDto> photos = photoService.getAllEventPhotosForAdmin(eventId, currentUser);
        return ResponseEntity.ok(ApiResponse.success(photos));
    }

    @PatchMapping("/photos/{id}/select")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PhotoDto>> toggleSelection(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> body) {
        User currentUser = securityUtils.getCurrentUser();
        Boolean isSelected = body.getOrDefault("isSelected", true);
        PhotoDto updated = photoService.togglePhotoSelection(id, isSelected, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Photo selection updated", updated));
    }

    @DeleteMapping("/photos/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePhoto(@PathVariable Long id) {
        User currentUser = securityUtils.getCurrentUser();
        photoService.deletePhoto(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Photo deleted successfully", null));
    }
}

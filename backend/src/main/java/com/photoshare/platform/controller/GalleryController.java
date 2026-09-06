package com.photoshare.platform.controller;

import com.photoshare.platform.dto.request.PinVerificationRequest;
import com.photoshare.platform.dto.request.PublishGalleryRequest;
import com.photoshare.platform.dto.response.ApiResponse;
import com.photoshare.platform.dto.response.GalleryDto;
import com.photoshare.platform.dto.response.PublicGalleryResponse;
import com.photoshare.platform.entity.User;
import com.photoshare.platform.security.SecurityUtils;
import com.photoshare.platform.service.GalleryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GalleryController {

    private final GalleryService galleryService;
    private final SecurityUtils securityUtils;

    @PostMapping("/events/{eventId}/gallery/publish")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<GalleryDto>> publishGallery(
            @PathVariable Long eventId,
            @Valid @RequestBody PublishGalleryRequest request) {
        User admin = securityUtils.getCurrentUser();
        GalleryDto published = galleryService.publishGallery(eventId, request, admin);
        return ResponseEntity.ok(ApiResponse.success("Gallery published successfully", published));
    }

    @GetMapping("/public/gallery/{galleryToken}")
    public ResponseEntity<ApiResponse<PublicGalleryResponse>> getPublicGalleryInfo(@PathVariable String galleryToken) {
        PublicGalleryResponse info = galleryService.getPublicGalleryInfo(galleryToken);
        return ResponseEntity.ok(ApiResponse.success(info));
    }

    @PostMapping("/public/gallery/{galleryToken}/verify-pin")
    public ResponseEntity<ApiResponse<PublicGalleryResponse>> verifyPin(
            @PathVariable String galleryToken,
            @Valid @RequestBody PinVerificationRequest request) {
        PublicGalleryResponse verified = galleryService.verifyPinAndGetPhotos(galleryToken, request.getPin());
        return ResponseEntity.ok(ApiResponse.success("PIN verified successfully", verified));
    }
}

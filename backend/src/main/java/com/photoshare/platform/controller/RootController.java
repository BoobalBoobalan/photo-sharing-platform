package com.photoshare.platform.controller;

import com.photoshare.platform.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RootController {

    @GetMapping("/")
    public ResponseEntity<ApiResponse<Map<String, String>>> rootHealthCheck() {
        Map<String, String> status = Map.of(
            "service", "Photo Sharing Platform Backend REST API",
            "status", "UP",
            "demoAdmin", "admin@photoshare.com",
            "demoGalleryToken", "abc123",
            "demoGalleryPin", "482917"
        );
        return ResponseEntity.ok(ApiResponse.success("API is running smoothly", status));
    }
}

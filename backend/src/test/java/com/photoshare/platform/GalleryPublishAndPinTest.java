package com.photoshare.platform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.photoshare.platform.dto.request.LoginRequest;
import com.photoshare.platform.dto.request.PinVerificationRequest;
import com.photoshare.platform.dto.request.PublishGalleryRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class GalleryPublishAndPinTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;

    @BeforeEach
    public void setup() throws Exception {
        LoginRequest adminLogin = new LoginRequest();
        adminLogin.setEmail("admin@photoshare.com");
        adminLogin.setPassword("Admin@123");

        MvcResult adminResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminLogin)))
                .andReturn();

        String adminBody = adminResult.getResponse().getContentAsString();
        adminToken = objectMapper.readTree(adminBody).get("data").get("token").asText();
    }

    @Test
    public void testPublicGalleryInfoRequiresPin() throws Exception {
        mockMvc.perform(get("/api/public/gallery/abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.eventTitle").exists())
                .andExpect(jsonPath("$.data.isPinRequired").value(true))
                .andExpect(jsonPath("$.data.photos").doesNotExist());
    }

    @Test
    public void testVerifyPinWithCorrectPin() throws Exception {
        PinVerificationRequest pinReq = new PinVerificationRequest();
        pinReq.setPin("482917");

        mockMvc.perform(post("/api/public/gallery/abc123/verify-pin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pinReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isPinVerified").value(true))
                .andExpect(jsonPath("$.data.photos").isArray())
                .andExpect(jsonPath("$.data.photos.length()").value(6));
    }

    @Test
    public void testVerifyPinWithIncorrectPin() throws Exception {
        PinVerificationRequest pinReq = new PinVerificationRequest();
        pinReq.setPin("000000");

        mockMvc.perform(post("/api/public/gallery/abc123/verify-pin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pinReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Incorrect PIN. Please try again."));
    }

    @Test
    public void testPublishGalleryWorkflow() throws Exception {
        PublishGalleryRequest publishReq = new PublishGalleryRequest();
        publishReq.setPin("999888");
        publishReq.setTitle("Updated Gallery Title");
        publishReq.setSelectedPhotoIds(List.of(1L, 2L, 3L));

        mockMvc.perform(post("/api/events/1/gallery/publish")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(publishReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.pin").value("999888"))
                .andExpect(jsonPath("$.data.isPublished").value(true));
    }
}

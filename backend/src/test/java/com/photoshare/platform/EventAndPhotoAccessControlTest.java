package com.photoshare.platform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.photoshare.platform.dto.request.CreateEventRequest;
import com.photoshare.platform.dto.request.LoginRequest;
import com.photoshare.platform.dto.response.JwtResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class EventAndPhotoAccessControlTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private String teamToken;

    @BeforeEach
    public void setup() throws Exception {
        // Admin Login
        LoginRequest adminLogin = new LoginRequest();
        adminLogin.setEmail("admin@photoshare.com");
        adminLogin.setPassword("Admin@123");

        MvcResult adminResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminLogin)))
                .andReturn();

        String adminBody = adminResult.getResponse().getContentAsString();
        adminToken = objectMapper.readTree(adminBody).get("data").get("token").asText();

        // Team Member Login
        LoginRequest teamLogin = new LoginRequest();
        teamLogin.setEmail("team@photoshare.com");
        teamLogin.setPassword("Team@123");

        MvcResult teamResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(teamLogin)))
                .andReturn();

        String teamBody = teamResult.getResponse().getContentAsString();
        teamToken = objectMapper.readTree(teamBody).get("data").get("token").asText();
    }

    @Test
    public void testAdminCanCreateEvent() throws Exception {
        CreateEventRequest request = new CreateEventRequest();
        request.setTitle("Corporate Gala 2026");
        request.setDescription("Annual Gala Night");
        request.setEventDate(LocalDate.now());

        mockMvc.perform(post("/api/events")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Corporate Gala 2026"));
    }

    @Test
    public void testTeamMemberCannotCreateEvent() throws Exception {
        CreateEventRequest request = new CreateEventRequest();
        request.setTitle("Unauthorized Event");
        request.setEventDate(LocalDate.now());

        mockMvc.perform(post("/api/events")
                .header("Authorization", "Bearer " + teamToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testTeamMemberCannotSelectPhotos() throws Exception {
        mockMvc.perform(patch("/api/photos/1/select")
                .header("Authorization", "Bearer " + teamToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"isSelected\": true}"))
                .andExpect(status().isForbidden());
    }
}

package com.photoshare.platform.config;

import com.photoshare.platform.entity.Event;
import com.photoshare.platform.entity.Gallery;
import com.photoshare.platform.entity.Photo;
import com.photoshare.platform.entity.Role;
import com.photoshare.platform.entity.User;
import com.photoshare.platform.repository.EventRepository;
import com.photoshare.platform.repository.GalleryRepository;
import com.photoshare.platform.repository.PhotoRepository;
import com.photoshare.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.imageio.ImageIO;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final PhotoRepository photoRepository;
    private final GalleryRepository galleryRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.storage.local.upload-dir:uploads}")
    private String uploadDir;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            log.info("Database already initialized with seed data.");
            return;
        }

        log.info("Initializing database with demo seed data...");

        // 1. Create Users
        User admin = userRepository.save(User.builder()
                .email("admin@photoshare.com")
                .password(passwordEncoder.encode("Admin@123"))
                .fullName("Alex Admin (Lead)")
                .role(Role.ROLE_ADMIN)
                .build());

        User teamMember1 = userRepository.save(User.builder()
                .email("team@photoshare.com")
                .password(passwordEncoder.encode("Team@123"))
                .fullName("David Pro (Photographer)")
                .role(Role.ROLE_TEAM_MEMBER)
                .build());

        User teamMember2 = userRepository.save(User.builder()
                .email("sarah@photoshare.com")
                .password(passwordEncoder.encode("Team@123"))
                .fullName("Sarah Lens (Assistant)")
                .role(Role.ROLE_TEAM_MEMBER)
                .build());

        // 2. Create Event: Arjun & Priya Wedding
        Set<User> team = new HashSet<>(List.of(teamMember1, teamMember2));
        Event weddingEvent = eventRepository.save(Event.builder()
                .title("Arjun & Priya Wedding")
                .description("Grand Wedding Ceremony & Reception held at Royal Palace Grounds.")
                .eventDate(LocalDate.now())
                .createdBy(admin)
                .assignedTeam(team)
                .build());

        // 3. Generate sample image files in upload directory
        Path eventUploadDir = Paths.get(uploadDir, "events", weddingEvent.getId().toString()).toAbsolutePath().normalize();
        Files.createDirectories(eventUploadDir);

        String[] sampleTitles = {
            "Ring Ceremony", "Varamala Moment", "Wedding Pheras", "Royal Reception",
            "Couple Portrait 1", "Bridal Entry", "Groom Entry", "Family Celebration"
        };
        Color[] sampleColors = {
            new Color(230, 81, 0), new Color(156, 39, 176), new Color(25, 118, 210), new Color(56, 142, 60),
            new Color(216, 27, 96), new Color(0, 150, 136), new Color(255, 179, 0), new Color(121, 85, 72)
        };

        for (int i = 0; i < sampleTitles.length; i++) {
            String filename = "sample_" + (i + 1) + ".jpg";
            Path imagePath = eventUploadDir.resolve(filename);
            createDummyImageFile(imagePath.toFile(), sampleTitles[i], sampleColors[i]);

            boolean isSelected = i < 6; // 6 out of 8 selected for published gallery
            User uploader = (i % 2 == 0) ? teamMember1 : teamMember2;

            photoRepository.save(Photo.builder()
                    .event(weddingEvent)
                    .uploadedBy(uploader)
                    .filename(filename)
                    .storageLocation("/uploads/events/" + weddingEvent.getId() + "/" + filename)
                    .fileSize(Files.size(imagePath))
                    .contentType("image/jpeg")
                    .isSelected(isSelected)
                    .build());
        }

        // 4. Create Published Gallery for the Event matching PDF sample token & PIN
        galleryRepository.save(Gallery.builder()
                .event(weddingEvent)
                .galleryToken("abc123")
                .pin("482917")
                .title("Arjun & Priya Wedding - Official Gallery")
                .isPublished(true)
                .build());

        log.info("Database seeding completed successfully!");
        log.info("Demo Admin: admin@photoshare.com / Admin@123");
        log.info("Demo Team Member: team@photoshare.com / Team@123");
        log.info("Demo Gallery Token: abc123 | PIN: 482917");
    }

    private void createDummyImageFile(File file, String text, Color bgColor) {
        try {
            int width = 800;
            int height = 600;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(bgColor);
            g.fillRect(0, 0, width, height);

            // Subtle gradient overlay
            GradientPaint gp = new GradientPaint(0, 0, new Color(255, 255, 255, 40), width, height, new Color(0, 0, 0, 80));
            g.setPaint(gp);
            g.fillRect(0, 0, width, height);

            // Watermark text
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 36));
            FontMetrics fm = g.getFontMetrics();
            int x = (width - fm.stringWidth(text)) / 2;
            int y = (height - fm.getHeight()) / 2 + fm.getAscent();
            g.drawString(text, x, y);

            g.setFont(new Font("SansSerif", Font.PLAIN, 18));
            String subText = "Photo Sharing Platform Demo";
            int subX = (width - g.getFontMetrics().stringWidth(subText)) / 2;
            g.drawString(subText, subX, y + 40);

            g.dispose();
            ImageIO.write(image, "jpg", file);
        } catch (IOException e) {
            log.error("Failed to generate sample image file", e);
        }
    }
}

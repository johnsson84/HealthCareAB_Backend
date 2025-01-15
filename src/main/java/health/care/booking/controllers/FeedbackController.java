package health.care.booking.controllers;

import health.care.booking.dto.FeedbackDTO;
import health.care.booking.models.Feedback;
import health.care.booking.respository.FeedbackRepository;
import health.care.booking.services.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final FeedbackRepository feedbackRepository;

    public FeedbackController(FeedbackService feedbackService, FeedbackRepository feedbackRepository) {
        this.feedbackService = feedbackService;
        this.feedbackRepository = feedbackRepository;
    };

    // Get all feedback
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<?> getAllFeedback() {
        return ResponseEntity.ok(feedbackRepository.findAll());
    }

    // Get all feedbacks from a caregiver
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @GetMapping("/caregiver/{caregiverUsername}")
    public ResponseEntity<?> getCaregiverFeedback(@Valid @PathVariable String caregiverUsername) throws Exception {
        List<Feedback> allFeedback = feedbackService.getFeedbackForCaregiver(caregiverUsername);
        if (!allFeedback.isEmpty()) {
            return ResponseEntity.ok(allFeedback);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No feedback found...");
        }
    }

    // Add a feedback to a caregiver
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/add")
    public ResponseEntity<?> addFeedback(@Valid @RequestBody FeedbackDTO feedbackDTO) {
        try {
            Feedback newFeedback = feedbackService.addFeedback(feedbackDTO);
            return ResponseEntity.ok(newFeedback);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Delete a feedback
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{feedbackId}")
    public ResponseEntity<?> deleteAnFeedback(@PathVariable String feedbackId) {
        try {
            return feedbackService.deleteFeedback(feedbackId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }
}

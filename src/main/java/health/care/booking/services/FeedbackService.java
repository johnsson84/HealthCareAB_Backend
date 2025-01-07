package health.care.booking.services;

import health.care.booking.dto.FeedbackDTO;
import health.care.booking.models.Appointment;
import health.care.booking.models.Feedback;
import health.care.booking.models.Status;
import health.care.booking.respository.AppointmentRepository;
import health.care.booking.respository.FeedbackRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final AppointmentRepository appointmentRepository;

    public FeedbackService(FeedbackRepository feedbackRepository, AppointmentRepository appointmentRepository) {
        this.feedbackRepository = feedbackRepository;
        this.appointmentRepository = appointmentRepository;
    }

    // Create a feedback
    public Feedback addFeedback(FeedbackDTO feedbackDTO) throws Exception {
        Appointment appointment = appointmentRepository.findById(feedbackDTO.getAppointmentId())
                .orElseThrow(() -> new Exception("Appointment not found!"));

        // Check if appointment is completed
        if (!appointment.getStatus().equals(Status.COMPLETED))  {
            throw new Exception("Appointment status is not set to COMPLETED");
        }

        // Check if feedback already given
        List<Feedback> all = getFeedbackForCaregiver(feedbackDTO.getCaregiverId());
        for (Feedback feedback : all) {
            if (feedback.getPatientUsername().equals(feedbackDTO.getPatientUsername())) {
                throw new Exception("Feedback already given!");
            }
        }

        // Create new feedback
        Feedback newFeedback = new Feedback();
        newFeedback.setAppointmentId(feedbackDTO.getAppointmentId());
        newFeedback.setCaregiverId(Optional.ofNullable(feedbackDTO.getCaregiverId()).orElse(""));
        newFeedback.setPatientUsername(Optional.ofNullable(feedbackDTO.getPatientUsername()).orElse(""));
        newFeedback.setComment(Optional.ofNullable(feedbackDTO.getComment()).orElse(""));
        newFeedback.setRating(Optional.of(feedbackDTO.getRating()).orElse(3));
        feedbackRepository.save(newFeedback);
        return newFeedback;
    }

    // Get a list of all feedback fo a caregiver
    public List<Feedback> getFeedbackForCaregiver(String caregiverId) {
        return feedbackRepository.findAllByCaregiverId(caregiverId);
    }

    // Delete a feedback
    public ResponseEntity<?> deleteFeedback(String feedbackId) throws Exception {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new Exception("Feedback does not exist, can't delete..."));
        feedbackRepository.delete(feedback);
        return ResponseEntity.ok("Feedback deleted successfully");
    }

}

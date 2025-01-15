package health.care.booking.services;

import health.care.booking.dto.FeedbackDTO;
import health.care.booking.models.*;
import health.care.booking.respository.AppointmentRepository;
import health.care.booking.respository.FeedbackRepository;
import health.care.booking.respository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    public FeedbackService(FeedbackRepository feedbackRepository, AppointmentRepository appointmentRepository, UserRepository userRepository) {
        this.feedbackRepository = feedbackRepository;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
    }

    // Create a feedback
    public Feedback addFeedback(FeedbackDTO feedbackDTO) throws Exception {
        Appointment appointment = appointmentRepository.findById(feedbackDTO.getAppointmentId())
                .orElseThrow(() -> new Exception("Appointment not found!"));

        // Check if appointment is completed
        if (!appointment.getStatus().equals(Status.COMPLETED))  {
            throw new Exception("Appointment status is not set to COMPLETED");
        }

        String patientUsername = userRepository.findById(appointment.getPatientId())
                .map(User::getUsername)
                .orElseThrow(() -> new Exception("Patient not found!"));

        String caregiverUsername = userRepository.findById(appointment.getCaregiverId())
                .map(User::getUsername)
                .orElseThrow(() -> new Exception("Caregiver not found!"));

        // Check if feedback already given
        List<Feedback> all = getFeedbackForCaregiver(caregiverUsername);
        for (Feedback feedback : all) {
            if (feedback.getPatientUsername().equals(patientUsername)) {
                throw new Exception("Feedback already given!");
            }
        }

        // Create new feedback
        Feedback newFeedback = new Feedback();
        newFeedback.setAppointmentId(feedbackDTO.getAppointmentId());
        newFeedback.setCaregiverUsername(Optional.ofNullable(caregiverUsername).orElse(""));
        newFeedback.setPatientUsername(Optional.ofNullable(patientUsername).orElse(""));
        newFeedback.setComment(Optional.ofNullable(feedbackDTO.getComment()).orElse(""));
        newFeedback.setRating(Optional.of(feedbackDTO.getRating()).orElse(3));
        feedbackRepository.save(newFeedback);
        return newFeedback;
    }

    // Get a list of all feedback fo a caregiver
    public List<Feedback> getFeedbackForCaregiver(String caregiverUsername) throws Exception {
        User caregiver = userRepository.findByUsername(caregiverUsername)
                .orElseThrow(() -> new RuntimeException("Caregiver not found!"));
        if (caregiver.getRoles().contains(Role.DOCTOR)) {
            return feedbackRepository.findAllByCaregiverUsername(caregiverUsername);
        } else {
            throw new Exception("Username is not a caregiver!");
        }

    }

    // Delete a feedback
    public ResponseEntity<?> deleteFeedback(String feedbackId) throws Exception {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new Exception("Feedback does not exist, can't delete..."));
        feedbackRepository.delete(feedback);
        return ResponseEntity.ok("Feedback deleted successfully");
    }

}

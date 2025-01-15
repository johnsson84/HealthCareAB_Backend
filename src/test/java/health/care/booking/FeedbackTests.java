package health.care.booking;

import health.care.booking.dto.FeedbackDTO;
import health.care.booking.models.*;
import health.care.booking.respository.AppointmentRepository;
import health.care.booking.respository.FeedbackRepository;
import health.care.booking.respository.UserRepository;
import health.care.booking.services.FeedbackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class FeedbackTests {
    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FeedbackService feedbackService;

    private User patient = new User();
    private User doctor = new User();
    private Appointment appointment = new Appointment();
    private Feedback savedFeedback = new Feedback();
    private FeedbackDTO feedbackDTO = new FeedbackDTO();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // ARRANGE
        // Setup a user
        patient.setId("1");
        patient.setUsername("feedbackUser");
        patient.setPassword("Feedback123");
        patient.setFirstName("Feedback");
        patient.setLastName("Feedbacksson");
        patient.setMail("feedback@feedback.com");

        // Setup a doctor
        doctor.setId("2");
        doctor.setUsername("doctorUser");
        doctor.setPassword("Doctor123");
        doctor.setFirstName("Doctor");
        doctor.setLastName("Doctorsson");
        doctor.setMail("doctor@feedback.com");
        doctor.setRoles(Set.of(Role.ADMIN));

        // Setup a appointment
        appointment.setId("3");
        appointment.setPatientId(patient.getId());
        appointment.setCaregiverId(doctor.getId());
        String appointmentTime = "2025-01-02T09:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime localDateTime = LocalDateTime.parse(appointmentTime, formatter);
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        appointment.setDateTime(date);
        appointment.setStatus(Status.COMPLETED);
    }

    // Used in multiple tests
    public void arrangeFeedback() {

        savedFeedback = new Feedback();
        savedFeedback.setId("4");
        savedFeedback.setAppointmentId(appointment.getId());
        savedFeedback.setCaregiverUsername(doctor.getUsername());
        savedFeedback.setPatientUsername(patient.getUsername());
        savedFeedback.setComment("comment");
        savedFeedback.setRating(4);

        feedbackDTO = new FeedbackDTO();
        feedbackDTO.setAppointmentId("3");
        feedbackDTO.setComment(savedFeedback.getComment());
        feedbackDTO.setRating(savedFeedback.getRating());
    }

    @Test
    void averageGradeTest() throws Exception {
        // Arrange
        doctor.setId("2");
        doctor.setUsername("doctorUser");
        doctor.setPassword("Doctor123");
        doctor.setFirstName("Doctor");
        doctor.setLastName("Doctorsson");
        doctor.setMail("doctor@feedback.com");
        doctor.setRoles(Set.of(Role.ADMIN));

        arrangeFeedback();
        Feedback feedback1 = new Feedback();
        feedback1.setRating(4);
        feedback1.setCaregiverUsername(doctor.getUsername());
        Feedback feedback2 = new Feedback();
        feedback2.setCaregiverUsername(doctor.getUsername());
        feedback2.setRating(5);
        List<Feedback> feedbackList = List.of(feedback1, feedback2);

        // Mock both repositories
        when(userRepository.findByUsername(doctor.getUsername())).thenReturn(Optional.of(doctor));
        when(feedbackRepository.findAllByCaregiverUsername(doctor.getUsername())).thenReturn(feedbackList);

        // Act
        double averageGrade = feedbackService.getAverageFeedbackGrade(doctor.getUsername());

        // Assert
        assertEquals(4.5, averageGrade, "Average grade calculation incorrect");
        System.out.println("Success! Average grade calculated correctly.");

        // Test with empty feedback list
        when(feedbackRepository.findAllByCaregiverUsername(doctor.getUsername())).thenReturn(List.of());

        // Act
        double emptyAverage = feedbackService.getAverageFeedbackGrade(doctor.getUsername());

        // Assert
        assertEquals(0.0, emptyAverage, "Empty feedback list should return 0.0");
        System.out.println("Success! Empty feedback list returns 0.0");
    }

    @Test
    public void addFeedbackToAnAppointment() throws Exception {

        // Arrange
        arrangeFeedback();

        when(feedbackRepository.save(any(Feedback.class))).thenReturn(savedFeedback);
        when(appointmentRepository.findById(any())).thenReturn(Optional.ofNullable(appointment));
        when(userRepository.findById(appointment.getPatientId())).thenReturn(Optional.ofNullable(patient));
        when(userRepository.findById(appointment.getCaregiverId())).thenReturn(Optional.ofNullable(doctor));
        when(userRepository.findByUsername(doctor.getUsername())).thenReturn(Optional.ofNullable(doctor));

        // Act
        Feedback feedback = feedbackService.addFeedback(feedbackDTO);

        // Assert
        assertEquals("3", feedback.getAppointmentId(), "appointmentId not saved");
        assertEquals("doctorUser", feedback.getCaregiverUsername(), "caregiverUsername not saved");
        assertEquals("feedbackUser", feedback.getPatientUsername(), "patientUsername not saved");
        assertEquals("comment", feedback.getComment(), "comment not saved");
        assertEquals(4, feedback.getRating(), "rating not saved");
        System.out.println("Success! Feedback added.");
    }

    @Test
    public void cantAddFeedbackToAppointmentThatDoesNotExist() throws Exception {

        // Arrange
        FeedbackDTO feedbackDTO = new FeedbackDTO();
        feedbackDTO.setAppointmentId("56");

        when(appointmentRepository.findById(any())).thenReturn(Optional.empty());

        // Act
        Exception exception = assertThrows(Exception.class, () -> {
            feedbackService.addFeedback(feedbackDTO);
        });

        // Assert
        assertEquals("Appointment not found!", exception.getMessage(), "Failed! Appointment exists...");
        System.out.println("Success! Appointment not found.");

    }

    @Test
    public void cantAddFeedbackTwice() throws Exception {
        // Arrange
        arrangeFeedback();

        when(feedbackRepository.save(any(Feedback.class))).thenReturn(savedFeedback);
        when(appointmentRepository.findById(any())).thenReturn(Optional.ofNullable(appointment));
        when(feedbackRepository.findAllByCaregiverUsername(any())).thenReturn(List.of(savedFeedback));
        when(userRepository.findById(appointment.getPatientId())).thenReturn(Optional.ofNullable(patient));
        when(userRepository.findById(appointment.getCaregiverId())).thenReturn(Optional.ofNullable(doctor));
        when(userRepository.findByUsername(doctor.getUsername())).thenReturn(Optional.ofNullable(doctor));

        // Act
        Exception exception = assertThrows(Exception.class, () -> {
            feedbackService.addFeedback(feedbackDTO);
        });

        // Assert
        assertEquals("Feedback already given!", exception.getMessage(), "Failed! Feedback wasn't given twice...");
        System.out.println("Success! Cant add feedback twice.");
    }

    @Test
    public void failIfAppointmentIsNotCompleted() throws Exception {
        // Arrange
        arrangeFeedback();
        appointment.setStatus(Status.SCHEDULED);

        when(appointmentRepository.findById(any())).thenReturn(Optional.ofNullable(appointment));
        when(feedbackRepository.findAllByCaregiverUsername(any())).thenReturn(List.of(savedFeedback));

        // Act
        Exception exception = assertThrows(Exception.class, () -> {
            feedbackService.addFeedback(feedbackDTO);
        });

        // Assert
        assertEquals("Appointment status is not set to COMPLETED", exception.getMessage(), "Failed! Appointment status is set to COMPLETED");
        System.out.println("Success! Failed to create feedback because appointment status is not COMPLETED.");
    }

    // Notering
    // Test for rating. Tänkte jag skulle gjort et test som kollar att man ger en rating mellan 1 och 5 men i och med
    // att vi använder @min @max på rating i modellen så kastar den fel innan den hinner köra någon kod.

}

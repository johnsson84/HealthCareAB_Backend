package health.care.booking;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import health.care.booking.models.Appointment;
import health.care.booking.models.Availability;
import health.care.booking.models.Status;
import health.care.booking.models.User;
import health.care.booking.respository.AppointmentRepository;
import health.care.booking.respository.AvailabilityRepository;
import health.care.booking.respository.UserRepository;
import health.care.booking.services.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Date;

public class AppointmentServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AvailabilityRepository availabilityRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private AppointmentService appointmentService;


    private User patient;
    private User caregiver;
    private Availability availability;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Sample user and caregiver data
        patient = new User();
        patient.setId("1");
        patient.setUsername("john_doe");

        caregiver = new User();
        caregiver.setId("2");
        caregiver.setUsername("caregiver_1");

        // Sample availability data
        availability = new Availability();
        availability.setId("1");
        availability.setCaregiverId(caregiver.getId());
        availability.setAvailableSlots(List.of(new Date(), new Date(System.currentTimeMillis() + 3600000))); // two slots
    }

    @Test
    void testCreateNewAppointment() {
        String username = "john_doe";
        String caregiverId = "2";
        String summary = "theSummary";
        Date availabilityDate = availability.getAvailableSlots().get(0);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(patient));
        when(userRepository.findById(caregiverId)).thenReturn(Optional.of(caregiver));
        when(availabilityRepository.findById(availability.getId())).thenReturn(Optional.of(availability));

        // Call service method
        Appointment appointment = appointmentService.createNewAppointment(username, summary, caregiverId, availabilityDate);

        // Validate that the appointment is created correctly
        assertNotNull(appointment);
        assertEquals(patient.getId(), appointment.getPatientId()); // Now comparing Strings
        assertEquals(caregiver.getId(), appointment.getCaregiverId()); // Now comparing Strings
        assertEquals(availabilityDate, appointment.getDateTime());
        assertEquals(Status.SCHEDULED, appointment.getStatus());
        assertEquals("theSummary", appointment.getSummary());
    }

    @Test
    void testSetPatientWhenUserNotFound() {
        String username = "non_existing_user";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            appointmentService.setPatient(username);
        });

        assertEquals("No user found with that Id.", exception.getMessage());
    }

    @Test
    void testSetCaregiverWhenUserNotFound() {
        String caregiverId = "non_existing_id";

        when(userRepository.findById(caregiverId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            appointmentService.setCaregiver(caregiverId);
        });

        assertEquals("No user found with that Id.", exception.getMessage());
    }


    @Test
    void testGetAppointmentWithNamesSuccess() {
        // Setup mock data
        Appointment appointment = new Appointment();
        appointment.setPatientId(patient.getId());
        appointment.setCaregiverId(caregiver.getId());
        appointment.setSummary("theSummary");
        appointment.setDateTime(new Date());
        appointment.setStatus(Status.SCHEDULED);

        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setMail("john.doe@example.com");

        caregiver.setUsername("caregiver_1");

        when(userRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
        when(userRepository.findById(caregiver.getId())).thenReturn(Optional.of(caregiver));

        // Call the method
        ResponseEntity<?> response = appointmentService.getAppointmentWithNames(appointment);

        // Validate response
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        // Validate response body
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("caregiver_1", responseBody.get("caregiverUsername"));
        assertEquals("John", responseBody.get("patientFirstName"));
        assertEquals("Doe", responseBody.get("patientLastName"));
        assertEquals("john.doe@example.com", responseBody.get("userEmail"));
        assertEquals("theSummary", responseBody.get("summary"));
        assertEquals(appointment.getDateTime(), responseBody.get("dateTime"));
        assertEquals(Status.SCHEDULED, responseBody.get("status"));
    }

    @Test
    void testGetAppointmentWithNamesPatientNotFound() {
        // Setup mock data
        Appointment appointment = new Appointment();
        appointment.setPatientId("non_existing_patient");
        appointment.setCaregiverId(caregiver.getId());

        when(userRepository.findById("non_existing_patient")).thenReturn(Optional.empty());

        // Call the method and expect an exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            appointmentService.getAppointmentWithNames(appointment);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testGetAppointmentWithNamesCaregiverNotFound() {
        // Setup mock data
        Appointment appointment = new Appointment();
        appointment.setPatientId(patient.getId());
        appointment.setCaregiverId("non_existing_caregiver");

        when(userRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
        when(userRepository.findById("non_existing_caregiver")).thenReturn(Optional.empty());

        // Call the method and expect an exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            appointmentService.getAppointmentWithNames(appointment);
        });

        assertEquals("User not found", exception.getMessage());
    }


    @Test
    void testGetAppointmentHistoryFromUsernamePatient() {
        // Arrange
        String userId = "patient123";
        Appointment appointment1 = new Appointment();
        appointment1.setStatus(Status.COMPLETED);
        appointment1.setDateTime(new Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000));

        Appointment appointment2 = new Appointment();
        appointment2.setStatus(Status.COMPLETED);
        appointment2.setDateTime(new Date(System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000));

        Appointment appointment3 = new Appointment();
        appointment3.setStatus(Status.ERROR);

        List<Appointment> appointments = List.of(appointment1, appointment2, appointment3);
        when(appointmentRepository.findAppointmentByPatientId(userId)).thenReturn(appointments);

        // Act
        ResponseEntity<?> response = appointmentService.getAppointmentHistoryFromUsernamePatient(userId);

        // Assert
        assertEquals(ResponseEntity.ok(List.of(appointment2, appointment1)), response);
        verify(appointmentRepository, times(1)).findAppointmentByPatientId(userId);
    }

    @Test
    void testGetAppointmentHistoryFromUsernameCaregiver() {
        // Arrange
        String userId = "caregiver123";
        Appointment appointment1 = new Appointment();
        appointment1.setStatus(Status.COMPLETED);
        appointment1.setDateTime(new Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000));

        Appointment appointment2 = new Appointment();
        appointment2.setStatus(Status.COMPLETED);
        appointment2.setDateTime(new Date(System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000));

        Appointment appointment3 = new Appointment();
        appointment3.setStatus(Status.ERROR);

        List<Appointment> appointments = List.of(appointment1, appointment2, appointment3);
        when(appointmentRepository.findAppointmentByCaregiverId(userId)).thenReturn(appointments);

        // Act
        ResponseEntity<?> response = appointmentService.getAppointmentHistoryFromUsernameCaregiver(userId);

        // Assert
        assertEquals(ResponseEntity.ok(List.of(appointment2, appointment1)), response);
        verify(appointmentRepository, times(1)).findAppointmentByCaregiverId(userId);
    }


}

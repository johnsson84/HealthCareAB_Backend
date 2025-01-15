package health.care.booking.services;

import health.care.booking.models.Appointment;
import health.care.booking.models.Status;
import health.care.booking.models.User;
import health.care.booking.respository.AppointmentRepository;
import health.care.booking.respository.AvailabilityRepository;
import health.care.booking.respository.UserRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    AvailabilityRepository availabilityRepository;

    public Appointment createNewAppointment(String patientId, String summary, String caregiverId, @NotNull Date availabilityDate) {
        Appointment newAppointment = new Appointment();
        newAppointment.setSummary(summary);
        newAppointment.setPatientId(setPatient(patientId));
        newAppointment.setCaregiverId(setCaregiver(caregiverId));
        newAppointment.setDateTime(availabilityDate);
        newAppointment.setStatus(Status.SCHEDULED);
        return newAppointment;
    }

    public String setPatient(String patientId) {
        User patient = userRepository.findByUsername(patientId)
                .orElseThrow(() -> new RuntimeException("No user found with that Id."));
        return patient.getId();
    }

    public String setCaregiver(String caregiverId) {
        User caregiver = userRepository.findById(caregiverId)
                .orElseThrow(() -> new RuntimeException("No user found with that Id."));
        return caregiver.getId();
    }


    public Status returnStatus(String status) {
        if (status.toUpperCase().equals(Status.CANCELLED.name())) {
            return Status.CANCELLED;
        } else if (status.toUpperCase().equals(Status.COMPLETED.name())) {
            return Status.COMPLETED;
        } else if (status.toUpperCase().equals(Status.SCHEDULED.name())) {
            return Status.SCHEDULED;
        } else {
            return Status.ERROR;
        }
    }

    public ResponseEntity<?> getAppointmentWithNames(Appointment appointment) {

        User foundPatient = userRepository.findById(appointment.getPatientId()).orElseThrow(() ->
                new RuntimeException("User not found")
        );
        User foundCaregiver = userRepository.findById(appointment.getCaregiverId()).orElseThrow(() ->
                new RuntimeException("User not found")
        );

        Map<String, Object> appointmentInfo = new HashMap<>();
        appointmentInfo.put("caregiverUsername", foundCaregiver.getUsername());
        appointmentInfo.put("patientFirstName", foundPatient.getFirstName());
        appointmentInfo.put("patientLastName", foundPatient.getLastName());
        appointmentInfo.put("userEmail", foundPatient.getMail());
        appointmentInfo.put("summary", appointment.getSummary());
        appointmentInfo.put("dateTime", appointment.getDateTime());
        appointmentInfo.put("status", appointment.getStatus());

        return ResponseEntity.ok(appointmentInfo);
    }


    public List<Appointment> getCompletedUserAppointments(String userId){
        List<Appointment> userAppointments = appointmentRepository.findAppointmentByPatientId(userId);

        // Filter to keep only scheduled appointments
        List<Appointment> historyAppointments = userAppointments.stream()
                .filter(appointment -> Status.SCHEDULED.equals(appointment.getStatus()))
                .filter(appointment -> {
                    LocalDateTime appointmentDateTime = appointment.getDateTime().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();

                    return appointmentDateTime.isAfter(LocalDateTime.now());
                })
                .collect(Collectors.toList());

        return historyAppointments;
    }
    public List<Appointment> getCompletedDoctorAppointments(String caregiverId) {
        List<Appointment> userAppointments = appointmentRepository.findByCaregiverId(caregiverId);

        // Filter to keep only scheduled appointments
        List<Appointment> historyAppointments = userAppointments.stream()
                .filter(appointment -> Status.SCHEDULED.equals(appointment.getStatus()))
                .filter(appointment -> {
                    LocalDateTime appointmentDateTime = appointment.getDateTime().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();

                    return appointmentDateTime.isAfter(LocalDateTime.now());
                })
                .collect(Collectors.toList());

        return historyAppointments;
    }
    public ResponseEntity<?> getAppointmentHistoryFromUsernamePatient(String userId) {

        List<Appointment> userAppointments = appointmentRepository.findAppointmentByPatientId(userId);

        // Filter to keep only completed appointments and sort by date (newest first)
        List<Appointment> historyAppointments = userAppointments.stream()
                .filter(appointment -> Status.COMPLETED.equals(appointment.getStatus()))
                .sorted((appointment1, appointment2) -> appointment2.getDateTime().compareTo(appointment1.getDateTime()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(historyAppointments);
    }

    public ResponseEntity<?> getAppointmentHistoryFromUsernameCaregiver(String userId) {

        List<Appointment> userAppointments = appointmentRepository.findAppointmentByCaregiverId(userId);

        // Filter to keep only completed appointments and sort by date (newest first)
        List<Appointment> historyAppointments = userAppointments.stream()
                .filter(appointment -> Status.COMPLETED.equals(appointment.getStatus()))
                .sorted((appointment1, appointment2) -> appointment2.getDateTime().compareTo(appointment1.getDateTime()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(historyAppointments);
    }
}

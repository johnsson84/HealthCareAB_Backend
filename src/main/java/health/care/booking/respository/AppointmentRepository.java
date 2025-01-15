package health.care.booking.respository;


import health.care.booking.models.Appointment;
import health.care.booking.models.Availability;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {
    //Optional<Appointment> findAppointmentByPatientIdOrCaregiverId(String id);
    List<Appointment> findAppointmentByPatientId(String patientId);
    List<Appointment> findByCaregiverId(String caregiverId);


    List<Appointment> findAppointmentByCaregiverId(String caregiverId);
}

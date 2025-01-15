package health.care.booking.respository;

import health.care.booking.models.Availability;
import health.care.booking.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvailabilityRepository extends MongoRepository<Availability, String> {
    List<Availability> findAll();
    List<Availability> findByCaregiverId(String caregiverId);
    Optional<Availability> findById(String id);

}

package health.care.booking.respository;
import health.care.booking.models.Facility;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface FacilityRepository  extends MongoRepository<Facility, String> {
Optional<Facility> findById(String id);
Optional<Facility> deleteFacilitiesById(String id);
}

package health.care.booking.respository;

import health.care.booking.models.Role;
import health.care.booking.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    List<User> findUserByRolesIs(Set<Role> roles);
    Optional<User> findByMail(String mail);
    boolean existsByMail(String mail);
    Optional<User> findById(String userId);
}


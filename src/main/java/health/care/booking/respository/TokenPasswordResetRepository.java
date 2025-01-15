package health.care.booking.respository;

import health.care.booking.models.TokenPasswordReset;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenPasswordResetRepository  extends MongoRepository<TokenPasswordReset, String> {
    Optional<TokenPasswordReset> findByToken(String token);

    Optional<TokenPasswordReset> deleteByMail(String mail);
}

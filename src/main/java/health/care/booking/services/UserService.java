package health.care.booking.services;


import health.care.booking.dto.AvailabilityUserIdResponse;

import health.care.booking.models.Role;
import health.care.booking.models.User;
import health.care.booking.respository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        // hash the password
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // ensure the user has at least the default role
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Set.of(Role.USER));
        }

        userRepository.save(user);
        return user;
    }

    public User registerCaregiver(User user) {
        // hash the password
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // ensure the user has at least the default role
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Set.of(Role.DOCTOR));
        }

        userRepository.save(user);
        return user;
    }

    public User registerAdmin(User user) {
        // hash the password
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // ensure the user has at least the default role
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Set.of(Role.ADMIN));
        }

        userRepository.save(user);
        return user;
    }


    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }


    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }


    public User findByUserId(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public List<AvailabilityUserIdResponse> makeAndSendBackUserResponse(List<String> userIds) {
        List<AvailabilityUserIdResponse> idResponses = new ArrayList<>();
        for (String id : userIds) {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Could not find user with id: " + id));
            AvailabilityUserIdResponse idResponse = new AvailabilityUserIdResponse(
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getUsername()
            );
            idResponses.add(idResponse);
        }
        return idResponses;
    }

}

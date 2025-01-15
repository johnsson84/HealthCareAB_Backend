package health.care.booking;

import health.care.booking.controllers.AuthController;
import health.care.booking.dto.RegisterRequest;
import health.care.booking.dto.RegisterResponse;
import health.care.booking.models.Role;
import health.care.booking.models.User;
import health.care.booking.respository.UserRepository;
import health.care.booking.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class SignUpTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(passwordEncoder.encode(any(CharSequence.class))).thenAnswer(invocation -> {
            return "encoded-" + invocation.getArgument(0); // Returnera en kodad version av lösenordet
        });

        userService.setPasswordEncoder(passwordEncoder);
    }

    /**
     * register new mock user
     */
    @Test
    public void testSignUp_Success() {

        // ARRANGE

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("username5");
        registerRequest.setPassword("password5");
        registerRequest.setMail("mail5@mail.com");
        registerRequest.setFirstName("firstName5");
        registerRequest.setLastName("lastName5");

        User userToSave = new User();
        userToSave.setUsername(registerRequest.getUsername());
        userToSave.setPassword(registerRequest.getPassword());
        userToSave.setMail(registerRequest.getMail());
        userToSave.setFirstName(registerRequest.getFirstName());
        userToSave.setLastName(registerRequest.getLastName());
        userToSave.setRoles(Set.of(Role.USER));

        User savedUser = new User();
        savedUser.setUsername(registerRequest.getUsername());
        savedUser.setPassword("encoded-password5");
        savedUser.setMail(registerRequest.getMail());
        savedUser.setFirstName(registerRequest.getFirstName());
        savedUser.setLastName(registerRequest.getLastName());
        savedUser.setRoles(Set.of(Role.USER));

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // ACT
        User userResult = userService.registerUser(userToSave);

        // ASSERT
        assertEquals("username5", userResult.getUsername(), "Saved user should have an username.");
        assertEquals("encoded-password5", userResult.getPassword(), "Saved user should have a hashed password.");
        assertEquals("mail5@mail.com", userResult.getMail(), "Saved user should have an mail.");
        assertEquals("firstName5", userResult.getFirstName(), "Saved user should have an first name.");
        assertEquals("lastName5", userResult.getLastName(), "Saved user should have an last name.");

        verify(userRepository, times(1)).save(any(User.class));
    }

}

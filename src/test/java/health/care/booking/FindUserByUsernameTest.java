package health.care.booking;

import health.care.booking.controllers.UserController;
import health.care.booking.models.User;
import health.care.booking.respository.UserRepository;
import health.care.booking.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


public class FindUserByUsernameTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindUserByUsername_Success() {
        // ARRANGE
        String username = "pedro";

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setFirstName("Pedro");
        mockUser.setLastName("Gomez");
        mockUser.setMail("pedro.gomez@mail.com");

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(mockUser));

        // ACT
        User result = userService.findByUsername(username);

        // ASSERT
        assertNotNull(result, "User should not be null");
        assertEquals(username, result.getUsername(), "Username should match");
        assertEquals("Pedro", result.getFirstName(), "First name should match");
        assertEquals("Gomez", result.getLastName(), "Last name should match");
        assertEquals("pedro.gomez@mail.com", result.getMail(), "Mail should match");

        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @Test
    public void testFindUserByUsername_NotFound() {
        // ARRANGE
        String username = "nonexistent";

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.findByUsername(username);
        }, "Expected UserNotFoundException to be thrown when user is not found");

        verify(userRepository, times(1)).findByUsername(anyString());
    }
}

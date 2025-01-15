package health.care.booking;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import health.care.booking.controllers.UserController;
import health.care.booking.models.User;
import health.care.booking.respository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindFullNameByUserId_UserExists() {
        // Arrange
        String userId = "123";
        User mockUser = new User();
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // Act
        ResponseEntity<?> response = userController.findFullNameByUserId(userId);

        // Assert
        assertEquals(ResponseEntity.ok("John Doe"), response);
        verify(userRepository, times(2)).findById(userId);
    }

    @Test
    void testFindFullNameByUserId_UserNotFound() {
        // Arrange
        String userId = "123";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = userController.findFullNameByUserId(userId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof String);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testFindFullNameByUserId_Exception() {
        // Arrange
        String userId = "123";
        when(userRepository.findById(userId)).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<?> response = userController.findFullNameByUserId(userId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Database error", response.getBody());
        verify(userRepository, times(1)).findById(userId);
    }
}

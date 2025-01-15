package health.care.booking;

import health.care.booking.models.TokenPasswordReset;
import health.care.booking.models.User;
import health.care.booking.respository.TokenPasswordResetRepository;
import health.care.booking.respository.UserRepository;
import health.care.booking.services.MailService;
import health.care.booking.services.PasswordResetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PasswordResetServiceTest {

    // Mockade beroenden för att simulera mailService tjänster och lösenords kryptering.
    @Mock
    private MailService mailService;
    @Mock
    private PasswordEncoder passwordEncoder;

    // Mockade beroenden för att simulera repository samt andra tjänster
    @Mock
    private TokenPasswordResetRepository tokenPasswordResetRepository;
    @Mock
    private UserRepository userRepository;

    // här injicerar jag dom mockade beroendena i passwordResetService
    @InjectMocks
    private PasswordResetService passwordResetService;

    // här initierar jag mock objekten innan varje test körs.
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Testar att skicka resetLink
    @Test
    public void testSendPasswordResetLink_Success() {
        // Arrange
        // String token = UUID.randomUUID().toString(); // Skapar en unik token
        String email = "johnmessoa@gmail.com"; // Detta blir E-posten dit återställningslänken skickas till.

        // Skapar ett mockat TokenPasswordReset objekt
        TokenPasswordReset mockToken = new TokenPasswordReset();
        mockToken.setEmail(email);
        mockToken.setToken("mocked-token");
        mockToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));

        // Mockar beteende för att ta bort den unika token som är kopplad till E-postadressen.
        when(tokenPasswordResetRepository.deleteByMail(email)).thenReturn(Optional.empty());

        // Mockar beteende för att kunna spara en ny genererad token.
        when(tokenPasswordResetRepository.save(any(TokenPasswordReset.class))).thenReturn(mockToken);

        // Act
        passwordResetService.sendPasswordResetLink(email); // Här anropar jag metoden som testas

        // Assert
        verify(tokenPasswordResetRepository, times(1)).deleteByMail(email); // Här verifierar jag att token raderas
        verify(tokenPasswordResetRepository, times(1)).save(any(TokenPasswordReset.class)); // verifierar att en ny token sparas
        verify(mailService, times(1)).sendEmail(eq(email), eq("Password Reset Request"), contains("http://localhost:5173/resetPassword")); // Verifierar att en korrekt länk skickas med i mejlet.
    }
    // Testar att det failar om e-posten inte finns i systemet
    @Test
    public void testSendPasswordResetLink_Failure() {
        // Arrange
        String email = "emailfinnsinte@gmail.com";

        // Mocka beteenden
        when(tokenPasswordResetRepository.deleteByMail(anyString())).thenReturn(Optional.empty());
        when(tokenPasswordResetRepository.save(any(TokenPasswordReset.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doThrow(new RuntimeException("Email sending failed")).when(mailService).sendEmail(
                eq(email), eq("Password Reset Request"), anyString());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            passwordResetService.sendPasswordResetLink(email);
        });

        // Kontrollera att rätt undantagsmeddelande kastades
        assertEquals("Email sending failed", exception.getMessage());

        // Verifiera anrop
        verify(tokenPasswordResetRepository, times(1)).deleteByMail(email);
        verify(tokenPasswordResetRepository, times(1)).save(any(TokenPasswordReset.class));
        verify(mailService, times(1)).sendEmail(eq(email), eq("Password Reset Request"), anyString());
    }
    // Testar att token med utgångsdatum i framtiden är giltig
    @Test
    public void testValidateToken_Success() {
        // Arrange
        String token = UUID.randomUUID().toString(); // unik token
        TokenPasswordReset mockToken = new TokenPasswordReset(); // genererar en mock-token
        mockToken.setToken(token); // Här sätter jag token värdet
        mockToken.setExpiryDate(LocalDateTime.now().plusMinutes(10)); // Här sätter jag utgångsdatumet till 10 minuter in i framtiden

        // Mockar beteendet för att hitta token i mongoDb
        when(tokenPasswordResetRepository.findByToken(token)).thenReturn(Optional.of(mockToken));

        // Act
        boolean result = passwordResetService.validateToken(token); // Här validerar jag token

        // Assert
        assertTrue(result, "Token should be valid"); // Kollar så att token är giltig.
    }

    // Testar att utgången token inte ska vara valid
    @Test
    public void testValidateToken_Failure() {
        // Arrange
        String token = UUID.randomUUID().toString(); // Unik token
        TokenPasswordReset mockToken = new TokenPasswordReset(); // En mock token
        mockToken.setToken(token); // Sätter token värde
        mockToken.setExpiryDate(LocalDateTime.now().minusMinutes(10)); // Sätter utgångsdatumet 10 minuter bakåt i tiden.

        // Mockar beteende för att hitta token i mongoDB
        when(tokenPasswordResetRepository.findByToken(token)).thenReturn(Optional.of(mockToken));

        // Act
        boolean result = passwordResetService.validateToken(token); // validering av token

        // Assert
        assertFalse(result, "Token should be invalid"); // kollar så att token är ogiltig
    }

    // testar att uppdatera lösenordet
    @Test
    public void testUpdatePassword_Success() {
        // Arrange
        String token = UUID.randomUUID().toString(); // Unik token
        String email = "johnmessoa@gmail.com"; // en e-post som är kopplad till en token
        String newPassword = "newPassword123"; // Nytt lösenord

        TokenPasswordReset mockToken = new TokenPasswordReset(); // skapar mock token
        mockToken.setToken(token); // sätter värdet
        mockToken.setEmail(email); // koppling token till email
        mockToken.setExpiryDate(LocalDateTime.now().plusMinutes(10)); // sätter utgångsdatum till 10 min framtid

        User mockUser = new User(); // Här skapar jag en mock user
        mockUser.setMail(email); // Sätter en user e-post
        mockUser.setPassword(newPassword);

        // Mockar beteende för att hitta en token och user i mongoDB
        when(tokenPasswordResetRepository.findByToken(token)).thenReturn(Optional.of(mockToken));
        when(userRepository.findByMail(email)).thenReturn(Optional.of(mockUser));

        // Mockar beteende för lösenordet
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedPassword");

        // Act
        passwordResetService.updatePassword(token, newPassword); //  Här uppdaterar jag lösenordet

        // Assert
        verify(userRepository, times(1)).save(mockUser); // kollar att user sparas
        assertEquals("encodedPassword", mockUser.getPassword(), "Password should be updated"); // kollar så att lösenordet är uppdaterat
        verify(tokenPasswordResetRepository, times(1)).delete(mockToken); // kollar att token är borttagen
    }

    // Testar att det failar om token har gått ut i tid
    @Test
    public void testUpdatePassword_Failure() {
        // Arrange
        String token = UUID.randomUUID().toString(); // Unik token
        TokenPasswordReset mockToken = new TokenPasswordReset(); // mock token
        mockToken.setToken(token); // sätter värdet
        mockToken.setExpiryDate(LocalDateTime.now().minusMinutes(10)); // utgångsdatum till minus 10 min i tiden

        // Mockar beteende för att hitta token i mongoDB
        when(tokenPasswordResetRepository.findByToken(token)).thenReturn(Optional.of(mockToken));

        // Act & Assert
        assertThrows(
                RuntimeException.class,
                () -> passwordResetService.updatePassword(token, "newPassword"),
                "Expected exception for the expired token"); // kollar att exception kastas när en token har gått ut i tid
    }
}

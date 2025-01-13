package health.care.booking.controllers;

import health.care.booking.dto.*;
import health.care.booking.models.Role;
import health.care.booking.models.TokenPasswordReset;
import health.care.booking.models.User;
import health.care.booking.respository.UserRepository;
import health.care.booking.services.CustomUserDetailsService;
import health.care.booking.services.PasswordResetService;
import health.care.booking.services.UserService;
import health.care.booking.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
// comment
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordResetService passwordResetService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request,
                                   HttpServletResponse response) {

        try {
            // authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // set authentication in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // get UserDetails
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // generate JWT token
            String jwt = jwtUtil.generateToken(userDetails);

            // generate JWT cookie
            ResponseCookie jwtCookie = ResponseCookie.from("jwt", jwt)
                    .httpOnly(true)
                    .secure(false) // OBS! set to true in production with HTTPS
                    .path("/")
                    .maxAge(10 * 60 * 60) // 10 hours
                    .sameSite("Strict") // "Strict", "Lax", or "None"
                    .build();

            // add cookie to response
            response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

            // return response without JWT in body
            AuthResponse authResponse = new AuthResponse(
                    "Login successful",
                    userDetails.getUsername(),
                    userService.findByUsername(userDetails.getUsername()).getRoles()
            );

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(authResponse);

        } catch (AuthenticationException e) {
            // Aauthentication failed
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Incorrect username or password");
        }
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {

        // check if the username already exists
        if (userService.existsByUsername(request.getUsername())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Username is already taken");
        }
        // check if the mail is already in use
        if (userRepository.existsByMail(request.getMail())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Mail is already taken");
        }

        // map the registration request to a User entity
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setMail(request.getMail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUserPictureURL(request.getUserPictureURL());

        // assign roles
        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            user.setRoles(Set.of(Role.USER));
        } else {
            user.setRoles(request.getRoles());
        }

        // register the user using UserService
        userService.registerUser(user);

        // create a response object
        RegisterResponse regResponse = new RegisterResponse(
                "User registered successfully",
                user.getUsername(),
                user.getRoles()
        );

        return ResponseEntity.ok(regResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // clear the JWT cookie by setting its maxAge to 0
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", null)
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

        // clear the SecurityContext
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok("Logged out successfully");
    }

    // check if user is authenticated
    @GetMapping("/check")
    public ResponseEntity<?> checkAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());

        return ResponseEntity.ok(new AuthResponse(
                "Authenticated",
                user.getUsername(),
                user.getRoles()
        ));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        passwordResetService.sendPasswordResetLink(email);
        return ResponseEntity.ok("Password reset link sent");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        passwordResetService.updatePassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Password successfully updated");
    }
}

package health.care.booking.controllers;

import health.care.booking.dto.AvailabilityRequest;
import health.care.booking.dto.ChangeAvailabilityRequest;
import health.care.booking.models.Availability;
import health.care.booking.models.Role;
import health.care.booking.models.User;
import health.care.booking.respository.AvailabilityRepository;
import health.care.booking.respository.UserRepository;
import health.care.booking.services.AvailabilityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/availability")
public class AvailabilityController {
    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    AvailabilityService availabilityService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/set/all")
    public ResponseEntity<?> setAvailabilityAll() {
        List<User> caregiverList = userRepository.findUserByRolesIs(Collections.singleton(Role.ADMIN));

        if (!availabilityService.loopCaregiverList(caregiverList)){
            return ResponseEntity.status(400).body("There are duplicate ");
        }
        return ResponseEntity.ok("All caregivers availability have been set.");
    }

    @PostMapping("/set/one")
    public ResponseEntity<?> setAvailabilityForOne(@Valid @RequestBody AvailabilityRequest availabilityRequest) {
        User careGiver = userRepository.findById(availabilityRequest.careGiverId)
                .orElseThrow(() -> new RuntimeException("Couldn't find any Caregiver Users"));
        Availability newAvailability = availabilityService.createNewAvailability(careGiver.getId());
        availabilityRepository.save(newAvailability);
        return ResponseEntity.ok("Added available times for user: " + careGiver.getUsername());
    }

    @GetMapping
    public List<Availability> getAllAvailability() {
        return availabilityRepository.findAll();
    }

    @GetMapping("/find-by-username/{username}")
    public List<Availability> getAvailabilityByUsername(@PathVariable String username){
        User user = userRepository.findByUsername(username.toString()).orElseThrow(() -> new RuntimeException("could not find user: " + username));
        return availabilityRepository.findByCaregiverId(user.getId());
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @PutMapping("/remove-availability")
    public ResponseEntity<?> removeAvailabilityHours(@Valid @RequestBody ChangeAvailabilityRequest changeAvailabilityRequest) {
        Availability changeDatesAvailability = availabilityRepository.findById(changeAvailabilityRequest.availabilityId).orElseThrow(() -> new RuntimeException("Could not find availability object"));
        availabilityService.removeAvailabilityByArray(changeAvailabilityRequest.changingDates, changeDatesAvailability);
        return ResponseEntity.ok("Availability changed");
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @PutMapping("/add-availability")
    public ResponseEntity<?> addAvailabilityHours(@Valid @RequestBody ChangeAvailabilityRequest changeAvailabilityRequest) {
        List<Date> changedDates = availabilityService.addAvailabilityByArray(changeAvailabilityRequest.changingDates, changeAvailabilityRequest.availabilityId);
        if (changedDates.isEmpty()){
            return ResponseEntity.ok("No dates have been added, most likely duplicates.");
        }
        return ResponseEntity.ok("Selected times were added: " + String.join(", ", changedDates.toString()));
    }
}
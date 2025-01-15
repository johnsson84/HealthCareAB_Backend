package health.care.booking.controllers;

import health.care.booking.dto.FacilityRequest;
import health.care.booking.models.Facility;
import health.care.booking.services.FacilityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/facility")
public class FacilityController {

    @Autowired
    private FacilityService facilityService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createFacility(@RequestBody FacilityRequest facilityRequest) {
        try {
            Facility createdFacility = facilityService.createFacility(facilityRequest);
            return ResponseEntity.ok("A new facility was added");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'DOCTOR')")
    @GetMapping("/all")
    public ResponseEntity<?> getAllFacilities() {
        try {
            List<Facility> facilities = facilityService.getAllFacilities();
            return ResponseEntity.ok(facilities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'DOCTOR')")
    @GetMapping("/one/{id}")
    public ResponseEntity<?> getOneFacility(@Valid @PathVariable String id) {
        try {
            return ResponseEntity.ok(facilityService.GetFacilityById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteFacility(@Valid @PathVariable String id) {
        try {
            facilityService.DeleteFacilityById(id);
            return ResponseEntity.ok("Facility deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}

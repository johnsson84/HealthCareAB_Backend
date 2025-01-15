package health.care.booking;

import health.care.booking.models.Availability;
import health.care.booking.models.User;
import health.care.booking.respository.AvailabilityRepository;
import health.care.booking.services.AvailabilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class AvailabilityTests {
    @Mock
    private AvailabilityRepository availabilityRepository;

    @InjectMocks
    private AvailabilityService availabilityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createNewAvailability_Positive() {
        User caregiver = new User();
        caregiver.setId(String.valueOf(1L)); // Mock a caregiver user

        when(availabilityRepository.findByCaregiverId(any(String.class))).thenReturn(new ArrayList<>());

        Availability availability = availabilityService.createNewAvailability(caregiver.getId());

        assertNotNull(availability);
        assertEquals(caregiver.getId(), availability.getCaregiverId());
        assertNotNull(availability.getAvailableSlots());
        assertTrue(!availability.getAvailableSlots().isEmpty());
        verify(availabilityRepository, times(1)).findByCaregiverId(caregiver.getId());
    }

    @Test
    void createNewAvailability_Negative_DuplicateSlots() {
        User caregiver = new User();
        caregiver.setId(String.valueOf(1L));

        Availability existingAvailability = availabilityService.createNewAvailability(caregiver.getId());

        when(availabilityRepository.findByCaregiverId(any(String.class))).thenReturn(List.of(existingAvailability));

        Exception exception = assertThrows(RuntimeException.class, () -> availabilityService.createNewAvailability(caregiver.getId()));

        assertNotNull(exception);
        verify(availabilityRepository, times(2)).findByCaregiverId(caregiver.getId());
    }

    @Test
    void loopCaregiverList_Positive() {
        List<User> caregiverList = new ArrayList<>();
        User caregiver1 = new User();
        caregiver1.setId(String.valueOf(1L));
        caregiverList.add(caregiver1);


        when(availabilityRepository.findByCaregiverId(any(String.class))).thenReturn(new ArrayList<>());

        boolean result = availabilityService.loopCaregiverList(caregiverList);

        assertTrue(result);
        verify(availabilityRepository, times(1)).saveAll(anyList());
    }

    @Test
    void loopCaregiverList_Negative_EmptyList() {
        List<User> caregiverList = new ArrayList<>();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            availabilityService.loopCaregiverList(caregiverList);
        });

        assertEquals("Couldn't find any caregivers", exception.getMessage());
    }

    @Test
    void createWeeklyAvailabilitySlots_Positive() {
        List<Date> slots = availabilityService.createWeeklyAvailabilitySlots();

        assertNotNull(slots);
        assertTrue(slots.size() > 0);

        LocalDate now = LocalDate.now();
        LocalDate twoWeeksLater = now.plusWeeks(2);

        for (Date date : slots) {
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            assertFalse(localDate.isBefore(now));
            assertTrue(localDate.isBefore(twoWeeksLater));
            assertTrue(localDate.getDayOfWeek().getValue() >= 1 && localDate.getDayOfWeek().getValue() <= 5);
        }
    }

    @Test
    void checkDuplicateAvailability_Positive_NoDuplicates() {
        User caregiver = new User();
        caregiver.setId(String.valueOf(1L));

        Availability newAvailability = new Availability();
        newAvailability.setCaregiverId(caregiver.getId());
        newAvailability.setAvailableSlots(List.of(new Date()));

        when(availabilityRepository.findByCaregiverId(caregiver.getId())).thenReturn(new ArrayList<>());

        boolean result = availabilityService.checkDuplicateAvailability(newAvailability);

        assertFalse(result);
        verify(availabilityRepository, times(1)).findByCaregiverId(caregiver.getId());
    }

    @Test
    void checkDuplicateAvailability_Negative_DuplicatesFound() {
        User caregiver = new User();
        caregiver.setId(String.valueOf(1L));

        Date slot = new Date();

        Availability newAvailability = new Availability();
        newAvailability.setCaregiverId(caregiver.getId());
        newAvailability.setAvailableSlots(List.of(slot));

        Availability existingAvailability = new Availability();
        existingAvailability.setCaregiverId(caregiver.getId());
        existingAvailability.setAvailableSlots(List.of(slot));

        when(availabilityRepository.findByCaregiverId(caregiver.getId())).thenReturn(List.of(existingAvailability));

        boolean result = availabilityService.checkDuplicateAvailability(newAvailability);

        assertTrue(result);
        verify(availabilityRepository, times(1)).findByCaregiverId(caregiver.getId());
    }

    @Test
    void removeAvailabilityByArray_Positive() {
        Availability availability = new Availability();
        List<Date> availableSlots = new ArrayList<>(List.of(
                new Date(1672531200000L),
                new Date(1672617600000L)));
        availability.setAvailableSlots(new ArrayList<>(availableSlots));

        List<Date> changingDates = new ArrayList<>(List.of(availableSlots.get(1)));

        availabilityService.removeAvailabilityByArray(changingDates, availability);

        assertEquals(1, availability.getAvailableSlots().size());
        verify(availabilityRepository, times(1)).save(availability);
    }

    @Test
    void removeAvailabilityByArray_Negative_NullInputs() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            availabilityService.removeAvailabilityByArray(null, null);
        });

        assertEquals("changingDates List is null or changingDatesAvailability", exception.getMessage());
    }

    @Test
    void addAvailabilityByArray_Positive() {
        // Mock inputs
        String availabilityId = "test-availability-id";
        List<Date> changingDates = Arrays.asList(new Date(System.currentTimeMillis() + 86400000), // Tomorrow
                new Date(System.currentTimeMillis() + 172800000)); // Day after tomorrow

        // Mock the Availability object and repository
        Availability mockAvailability = new Availability();
        mockAvailability.setAvailableSlots(new ArrayList<>());

        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.of(mockAvailability));

        // Invoke the method
        List<Date> validDates = availabilityService.addAvailabilityByArray(changingDates, availabilityId);

        // Assertions
        assertNotNull(validDates);
        assertEquals(changingDates.size(), validDates.size());
        assertTrue(mockAvailability.getAvailableSlots().containsAll(validDates));
        verify(availabilityRepository, times(1)).save(mockAvailability);
    }

    @Test
    void addAvailabilityByArray_Negative_InvalidDatesBeforeNow() {
        // Mock inputs
        String availabilityId = "test-availability-id";
        List<Date> changingDates = Arrays.asList(new Date(System.currentTimeMillis() - 86400000), // Yesterday
                new Date(System.currentTimeMillis() + 86400000)); // Tomorrow

        // Mock the Availability object and repository
        Availability mockAvailability = new Availability();
        mockAvailability.setAvailableSlots(new ArrayList<>());

        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.of(mockAvailability));

        // Assertions
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            availabilityService.addAvailabilityByArray(changingDates, availabilityId);
        });

        assertTrue(exception.getMessage().contains("Invalid dates before now found"));
        verify(availabilityRepository, never()).save(any(Availability.class));
    }


}

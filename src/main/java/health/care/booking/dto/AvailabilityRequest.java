package health.care.booking.dto;

import jakarta.validation.constraints.NotBlank;

public class AvailabilityRequest {
    @NotBlank
    public String careGiverId;
}

package health.care.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public class AppointmentRequest {

    @NotBlank
    public String username;


    @NotBlank
    public String summary;

    @NotBlank
    public String caregiverId;

    @NotBlank
    public String availabilityId;

    @NotNull
    public Date availabilityDate;

    public AppointmentRequest() {
    }


}
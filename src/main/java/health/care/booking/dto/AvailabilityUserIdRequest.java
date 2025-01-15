package health.care.booking.dto;

import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AvailabilityUserIdRequest {
    @NotNull
    List<String> userIds = new ArrayList<>();

    public List<String> getUserIds() {
        return userIds;
    }

}

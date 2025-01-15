package health.care.booking.dto;

import java.util.ArrayList;
import java.util.List;

public class AvailabilityUserIdResponse {
    private String caregiverId;
    private String firstname;
    private String lastname;
    private String username;

    public AvailabilityUserIdResponse(String caregiverId, String firstname, String lastname, String username) {
        this.caregiverId = caregiverId;
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
    }

    public String getCaregiverId() {
        return caregiverId;
    }

    public void setCaregiverId(String caregiverId) {
        this.caregiverId = caregiverId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

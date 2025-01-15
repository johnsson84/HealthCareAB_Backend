package health.care.booking.dto;


import health.care.booking.models.Role;

import java.util.Set;

public class RegisterResponse {

    private String message;
    private String username;
    private Set<Role> roles;


    public RegisterResponse() {
    }

    public RegisterResponse(String message, String username, Set<Role> roles) {
        this.message = message;
        this.username = username;
        this.roles = roles;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}

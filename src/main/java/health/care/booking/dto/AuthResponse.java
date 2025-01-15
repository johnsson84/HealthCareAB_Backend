package health.care.booking.dto;

import health.care.booking.models.Role;
import java.util.Set;

public class AuthResponse {
    private String jwtToken;
    private String username;
    private Set<Role> roles;

    public AuthResponse() {
    }

    public AuthResponse(String jwtToken, String username, Set<Role> roles) {
        this.jwtToken = jwtToken;
        this.username = username;
        this.roles = roles;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
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


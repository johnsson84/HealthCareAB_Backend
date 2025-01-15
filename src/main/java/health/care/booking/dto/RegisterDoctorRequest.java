package health.care.booking.dto;

import health.care.booking.models.Role;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public class RegisterDoctorRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
    @NotBlank
    private String mail;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;

    private Set<Role> roles;

    @NotBlank
    private String specialities;

    // location
    // profile picture

    public RegisterDoctorRequest() {
    }

    public RegisterDoctorRequest(String username, String password, String mail, Set<Role> roles, String firstName,
                                 String lastName, String specialities) {
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.mail = mail;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialities = specialities;
    }

    public @NotBlank String getUsername() {
        return username;
    }

    public @NotBlank String getPassword() {
        return password;
    }

    public @NotBlank String getMail() {
        return mail;
    }

    public @NotBlank String getFirstName() {
        return firstName;
    }

    public @NotBlank String getLastName() {
        return lastName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getSpecialities() {
        return specialities;
    }

    public void setSpecialities(String specialities) {
        this.specialities = specialities;
    }

    public Set<Role> getRoles() {
        return roles;
    }
}

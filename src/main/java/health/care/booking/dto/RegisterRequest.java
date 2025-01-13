package health.care.booking.dto;

import health.care.booking.models.Role;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public class RegisterRequest {

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

    private String userPictureURL;

    private Set<Role> roles;

    public RegisterRequest() {
    }

    public RegisterRequest(String username, String password, Set<Role> roles, String mail, String firstName,
            String lastName, String userPictureURL) {
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.mail = mail;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userPictureURL = userPictureURL;
    }

    public String getUserPictureURL() {
        return userPictureURL;
    }

    public void setUserPictureURL(String userPictureURL) {
        this.userPictureURL = userPictureURL;
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

    public Set<Role> getRoles() {
        return roles;
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

}

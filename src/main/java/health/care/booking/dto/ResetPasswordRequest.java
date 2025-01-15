package health.care.booking.dto;

import jakarta.validation.constraints.NotBlank;

public class ResetPasswordRequest {
    @NotBlank
    private String token;
    @NotBlank
    private String newPassword;

    public @NotBlank String getNewPassword() {
        return newPassword;
    }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    public @NotBlank String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}

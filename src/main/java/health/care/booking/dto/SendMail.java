package health.care.booking.dto;

import jakarta.validation.constraints.NotBlank;

public class SendMail {
    @NotBlank
    private String toEmail;

    private String subject;

    private String text;

    private String appointmentSummary;

    private String time;
    private String date;
    private String firstName;

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAppointmentSummary() {
        return appointmentSummary;
    }

    public void setAppointmentSummary(String appointmentSummary) {
        this.appointmentSummary = appointmentSummary;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}

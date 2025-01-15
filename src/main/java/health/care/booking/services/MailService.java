package health.care.booking.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String toEmail, String subject, String text) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(text);
        mailSender.send(mailMessage);
    }

    public void sendEmailAppointment(String toEmail, String subject, String text, String time, String date, String appointmentSummary) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText("Your appointment on: " + date + " at the time of: " + time + ". concerning: " + appointmentSummary + " has a pending message: " + text);
        mailSender.send(mailMessage);
    }

    public void sendEmailRequest(String toEmail, String appointmentSummary, String time, String date, String firstName) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject("Mail request from: " + firstName);
        mailMessage.setText("You have requested an email concerning: " + appointmentSummary + ". your booked appointment is on " + date + " time: " + time + ". Please write below why you requested this mail:");
        mailSender.send(mailMessage);
    }
}

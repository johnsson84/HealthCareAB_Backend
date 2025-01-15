package health.care.booking.dto;

import java.util.List;

public class FeedbackAverageGradeAllResponse {

    String username;
    double averageGrade;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getAverageGrade() {
        return averageGrade;
    }

    public void setAverageGrade(double averageGrade) {
        this.averageGrade = averageGrade;
    }
}

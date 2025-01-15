package health.care.booking.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Document(collection = "availability")
public class Availability {
    @Id
    private String id;

    // en doktor/sjuksköterska sätter sig available
    private String caregiverId;

    // en lista med tider som är tillgängliga
    // ni kan ändra implementaionen om ni hittar ett enklare sätt
    private List<Date> availableSlots;

    public Availability() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCaregiverId() {
        return caregiverId;
    }

    public void setCaregiverId(String caregiverId) {
        this.caregiverId = caregiverId;
    }

    public List<Date> getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(List<Date> availableSlots) {
        this.availableSlots = availableSlots;
    }
}

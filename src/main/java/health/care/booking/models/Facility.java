package health.care.booking.models;

import health.care.booking.dto.CoworkerRequest;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "facility")
public class Facility {

  @Id
  private String id;

  //Most of these should probably have @notNull
  private String facilityName;
  // Should probably either be a Long or a string
  private int phoneNumber;
  private String email;
  private String hoursOpen;
  private List<CoworkerRequest> coworkers;
  private FacilityAddress facilityAddress;

  public Facility() {

  }

  public String getFacilityName() {
    return facilityName;
  }

  public void setFacilityName(String facilityName) {
    this.facilityName = facilityName;
  }

  public int getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(int phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getHoursOpen() {
    return hoursOpen;
  }

  public void setHoursOpen(String hoursOpen) {
    this.hoursOpen = hoursOpen;
  }

  public String getId() {
    return id;
  }

  public List<CoworkerRequest> getCoworkers() {
    return coworkers;
  }

  public void setCoworkers(List<CoworkerRequest> coworkers) {
    this.coworkers = coworkers;
  }

  public FacilityAddress getAddress() {
    return facilityAddress;
  }

  public void setAddress(FacilityAddress facilityAddress) {
    this.facilityAddress = facilityAddress;
  }
}

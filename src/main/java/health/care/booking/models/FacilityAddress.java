package health.care.booking.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "facilityAddress")
public class FacilityAddress {

  private String street;
  private String city;
  private int postCode;
  private String region;
  private String country;

  public FacilityAddress() {
  }

  public FacilityAddress(String street, String city, int postCode, String region, String country) {
    this.street = street;
    this.city = city;
    this.postCode = postCode;
    this.region = region;
    this.country = country;
  }


  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public int getPostCode() {
    return postCode;
  }

  public void setPostCode(int postCode) {
    this.postCode = postCode;
  }

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }
}

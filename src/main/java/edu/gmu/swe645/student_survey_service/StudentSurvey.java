package edu.gmu.swe645.student_survey_service;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "student_survey")
@Data                         // <‑‑ Lombok generates getters, setters, toString, equals, hashCode
public class StudentSurvey {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    public void setFirstName(String firstName) { this.firstName = firstName; }
    private String lastName;
    public void setLastName(String lastName) { this.lastName = lastName; }
    private String streetAddress;
    public void setCity(String city) { this.city = city; }
    private String city;
    public void setState(String state) { this.state = state; }
    private String state;
    public void setZip(String zip) { this.zip = zip; }
    private String zip;
    public void setTelephone(String telephone) { this.telephone = telephone; }
    private String telephone;
    public void setEmail(String email) { this.email = email; }
    private String email;
    private LocalDate dateOfSurvey; 

    private String likedMost;
    public void setLikedMost(String likedMost) { this.likedMost = likedMost; }
    private String interestSource;
    public void setInterestSource(String interestSource) { this.interestSource = interestSource; }
    private String recommendation;
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

     @Transient
    @JsonProperty
    public String getFullName() {
        String fName = (firstName != null ? firstName : "");
        String lName = (lastName != null ? lastName : "");
        return fName + (fName.isEmpty() || lName.isEmpty() ? "" : " ") + lName;
    }
}

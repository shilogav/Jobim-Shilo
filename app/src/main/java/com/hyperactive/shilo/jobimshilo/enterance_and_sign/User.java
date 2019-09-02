package com.hyperactive.shilo.jobimshilo.enterance_and_sign;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;


//class for user
@IgnoreExtraProperties
public class User {

    private String userPhoneNumber;
    private String username;
    private String email;
    private String city;
    private String dateOfBirth;
    private ArrayList<String> favoriteJobs;
    private ArrayList<String> interestJobs;

    public ArrayList<String> getFavoriteJobs() {
        if(favoriteJobs!=null)
            return favoriteJobs;
        return new ArrayList<String>();
    }

    public void setFavoriteJobs(ArrayList<String> favoriteJobs) {
        this.favoriteJobs = favoriteJobs;
    }

    public ArrayList<String> getInterestJobs() {
        if(interestJobs!=null)
            return interestJobs;
        return new ArrayList<String>();
    }

    public void setInterestJobs(ArrayList<String> interestJobs) {
        this.interestJobs = interestJobs;
    }

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
        this.username="";
        this.email="";
        this.city="";
        this.dateOfBirth="";
        this.favoriteJobs=new ArrayList<String>();
        this.interestJobs=new ArrayList<String>();
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }
}




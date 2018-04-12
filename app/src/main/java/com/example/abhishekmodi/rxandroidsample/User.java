package com.example.abhishekmodi.rxandroidsample;

import android.location.Address;

/**
 * Created by abhishek.modi on 4/12/2018.
 */

public class User {
    String name;
    String email;
    String gender;
    String address;

    // getters and setters


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
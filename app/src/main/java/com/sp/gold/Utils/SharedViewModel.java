package com.sp.gold.Utils;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {
    private String nric = "";
    private String name = "";
    private String phone = "";
    private ArrayList<String> address = new ArrayList<>();
    public boolean nricVerified = false;
    public boolean phoneVerified = false;

    public boolean isNricVerified() {
        return nricVerified;
    }

    public void setNricVerified(boolean nricVerified) {
        this.nricVerified = nricVerified;
    }

    public boolean isPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNric() {
        return nric;
    }

    public void setNric(String data) {
        this.nric = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAddress() {
        return address;
    }

    public void setAddress(ArrayList<String> address) {
        this.address = address;
    }
}
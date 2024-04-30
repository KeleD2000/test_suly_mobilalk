package com.example.test_suly_mon;

import java.util.ArrayList;

public class User {
    public String email;
    public ArrayList <String> kg;
    public String magassag;

    public void setMagassag(String magassag) {
        this.magassag = magassag;
    }

    public String getMagassag() {
        return magassag;
    }

    public String getEmail() {
        return email;
    }

    public ArrayList<String> getKg() {
        return kg;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setKg(ArrayList<String> kg) {
        this.kg = kg;
    }
}

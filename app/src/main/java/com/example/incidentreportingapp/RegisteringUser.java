package com.example.incidentreportingapp;

import java.util.Objects;

public class RegisteringUser {
    private String fName, lName, contact, email;

    @Override
    public String toString() {
        return "RegisteringUser{" +
                "fName='" + fName + '\'' +
                ", lName='" + lName + '\'' +
                ", contact='" + contact + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public RegisteringUser() {
    }

    public RegisteringUser(String fName, String lName, String contact, String email) {
        this.fName = fName;
        this.lName = lName;
        this.contact = contact;
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisteringUser that = (RegisteringUser) o;
        return Objects.equals(fName, that.fName) && Objects.equals(lName, that.lName) && Objects.equals(contact, that.contact) && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fName, lName, contact, email);
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getfName() {
        return fName;
    }

    public String getlName() {
        return lName;
    }

    public String getContact() {
        return contact;
    }

    public String getEmail() {
        return email;
    }
}

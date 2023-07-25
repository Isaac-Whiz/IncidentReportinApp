package com.example.incidentreportingapp;

import java.util.Objects;

public class RegisteringUsers {
    private String fName, lName, contact, email;

    public RegisteringUsers(String fName, String lName, String contact, String email) {
        this.fName = fName;
        this.lName = lName;
        this.contact = contact;
        this.email = email;
    }

    @Override
    public String toString() {
        return "RegisteringUsers{" +
                "fName='" + fName + '\'' +
                ", lName='" + lName + '\'' +
                ", contact='" + contact + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public RegisteringUsers() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisteringUsers that = (RegisteringUsers) o;
        return Objects.equals(fName, that.fName) && Objects.equals(lName, that.lName) && Objects.equals(contact, that.contact) && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fName, lName, contact, email);
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

package com.example.incidentreportingapp;

import java.util.Objects;

public class RegisteringUsers {
    private String fName, lName, contact, email;

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
}

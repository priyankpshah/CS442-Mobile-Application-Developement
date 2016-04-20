package edu.iit.cs442.team15.ehome.model;

public class Owner {
    public int id;
    public String complexName;
    public String ownerPhone;
    public String ownerEmail;

    public Owner setId(int id) {
        this.id = id;
        return this;
    }

    public Owner setComplexName(String complexName) {
        this.complexName = complexName;
        return this;
    }

    public Owner setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
        return this;
    }

    public Owner setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
        return this;
    }

}

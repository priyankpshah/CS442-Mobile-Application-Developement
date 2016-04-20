package edu.iit.cs442.team15.ehome.model;

public class WebApartment {
    public String name;
    public String address;
    public double rent;
    public double latitude;
    public double longitude;
    public String ownerEmail;
    public String ownerPhone;
    public String ownerWebsite;

    public WebApartment setName(String name) {
        this.name = name;
        return this;
    }

    public WebApartment setAddress(String address) {
        this.address = address;
        return this;
    }

    public WebApartment setRent(double rent) {
        this.rent = rent;
        return this;
    }

    public WebApartment setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public WebApartment setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public WebApartment setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
        return this;
    }

    public WebApartment setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
        return this;
    }

    public WebApartment setOwnerWebsite(String ownerWebsite) {
        this.ownerWebsite = ownerWebsite;
        return this;
    }
    
}

package edu.iit.cs442.team15.ehome.model;

import java.io.Serializable;

public class Apartment implements Serializable {

    public int id;
    public String address;
    public int zip;
    public int bedrooms;
    public int bathrooms;
    public double squareFeet;
    public double rent;
    public Owner owner;
    public Amenity amenity;

    public double getTotalCost() {
        return amenity != null ? rent + amenity.getTotalCost() : rent;
    }

    public Apartment setId(int id) {
        this.id = id;
        return this;
    }

    public Apartment setAddress(String address) {
        this.address = address;
        return this;
    }

    public Apartment setZip(int zip) {
        this.zip = zip;
        return this;
    }

    public Apartment setBedrooms(int bedrooms) {
        this.bedrooms = bedrooms;
        return this;
    }

    public Apartment setBathrooms(int bathrooms) {
        this.bathrooms = bathrooms;
        return this;
    }

    public Apartment setSquareFeet(double squareFeet) {
        this.squareFeet = squareFeet;
        return this;
    }

    public Apartment setRent(double rent) {
        this.rent = rent;
        return this;
    }

    public Apartment setOwner(Owner owner) {
        this.owner = owner;
        return this;
    }

    public Apartment setAmenity(Amenity amenity) {
        this.amenity = amenity;
        return this;
    }

}

package edu.iit.cs442.team15.ehome.model;

import java.io.Serializable;
import java.util.Comparator;

public class Apartment implements Serializable {

    public int id;
    public String address;
    public int zip;
    public int bedrooms;
    public int bathrooms;
    public double square_feet;
    public int rent;
    public int owner_id;

    public Apartment() {
    }

    public Apartment(int id, String address, int zip, int bedrooms, int bathrooms, double square_feet, int rent, int owner_id) {
        this.id = id;
        this.address = address;
        this.zip = zip;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.square_feet = square_feet;
        this.rent = rent;
        this.owner_id = owner_id;
    }

    public static Comparator<Apartment> rentComparator = new Comparator<Apartment>() {
        @Override
        public int compare(Apartment lhs, Apartment rhs) {
            return lhs.rent > rhs.rent ? 1 : 0;
        }
    };

    public static Comparator<Apartment> areaComparator = new Comparator<Apartment>() {
        @Override
        public int compare(Apartment lhs, Apartment rhs) {
            return lhs.square_feet < rhs.square_feet ? 1 : 0;
        }
    };

}

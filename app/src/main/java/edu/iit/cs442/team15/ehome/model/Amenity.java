package edu.iit.cs442.team15.ehome.model;

public class Amenity {

    public int apartmentId;
    public boolean parking;
    public boolean gym;
    public double gas;
    public double electricity;
    public double internet;
    public double cable;
    public double thermostat;

    public double getTotalCost() {
        return gas + electricity + internet + cable + thermostat;
    }

    public Amenity setApartmentId(int apartmentId) {
        this.apartmentId = apartmentId;
        return this;
    }

    public Amenity setParking(boolean parking) {
        this.parking = parking;
        return this;
    }

    public Amenity setGym(boolean gym) {
        this.gym = gym;
        return this;
    }

    public Amenity setGas(double gas) {
        this.gas = gas;
        return this;
    }

    public Amenity setElectricity(double electricity) {
        this.electricity = electricity;
        return this;
    }

    public Amenity setInternet(double internet) {
        this.internet = internet;
        return this;
    }

    public Amenity setCable(double cable) {
        this.cable = cable;
        return this;
    }

    public Amenity setThermostat(double thermostat) {
        this.thermostat = thermostat;
        return this;
    }

}

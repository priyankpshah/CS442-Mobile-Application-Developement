package edu.iit.cs442.team15.ehome.model;

public class Amenities {

    public int apartmentId;
    public boolean parking;
    public boolean gym;
    public double gas;
    public double electricity;
    public double internet;
    public double cable;
    public double thermostat;

    public Amenities setApartmentId(int apartmentId) {
        this.apartmentId = apartmentId;
        return this;
    }

    public Amenities setParking(boolean parking) {
        this.parking = parking;
        return this;
    }

    public Amenities setGym(boolean gym) {
        this.gym = gym;
        return this;
    }

    public Amenities setGas(double gas) {
        this.gas = gas;
        return this;
    }

    public Amenities setElectricity(double electricity) {
        this.electricity = electricity;
        return this;
    }

    public Amenities setInternet(double internet) {
        this.internet = internet;
        return this;
    }

    public Amenities setCable(double cable) {
        this.cable = cable;
        return this;
    }

    public Amenities setThermostat(double thermostat) {
        this.thermostat = thermostat;
        return this;
    }

}

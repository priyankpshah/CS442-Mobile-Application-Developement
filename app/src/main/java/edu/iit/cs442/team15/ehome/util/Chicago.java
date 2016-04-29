package edu.iit.cs442.team15.ehome.util;

public interface Chicago {
    double LATITUDE = 41.8781136;
    double LONGITUDE = -87.6297982;

    // bounding box for geocoder
    double LOWER_LEFT_LONG = -87.884445;
    double LOWER_LEFT_LAT = 41.705438;
    double UPPER_RIGHT_LONG = -87.52121;
    double UPPER_RIGHT_LAT = 42.050754;

    // conversion factors
    double METERS_TO_MILES = 0.000621371;
    double MILES_TO_METERS = 1609.34;
}

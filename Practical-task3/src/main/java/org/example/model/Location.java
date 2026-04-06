package org.example.model;

public class Location {
    private int locationId;
    private String locationName;

    public Location(int locationId, String locationName) {
        this.locationId = locationId;
        this.locationName = locationName;
    }

    public int getLocationId() { return locationId; }
    public String getLocationName() { return locationName; }
}
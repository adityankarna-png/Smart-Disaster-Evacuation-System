package com.disaster.earthquake;

public class EarthquakeData {

    private final String id;
    private final double magnitude;
    private final String place;
    private final long time;
    private final double longitude;
    private final double latitude;
    private final double depth;
    private final boolean tsunami;
    private final String alert;
    private final String status;

    public EarthquakeData(
            String id,
            double magnitude,
            String place,
            long time,
            double longitude,
            double latitude,
            double depth,
            boolean tsunami,
            String alert,
            String status
    ) {

        this.id = id;
        this.magnitude = magnitude;
        this.place = place;
        this.time = time;
        this.longitude = longitude;
        this.latitude = latitude;
        this.depth = depth;
        this.tsunami = tsunami;
        this.alert = alert;
        this.status = status;
    }


    public String getId() {
        return id;
    }


    public double getMagnitude() {
        return magnitude;
    }


    public String getPlace() {
        return place;
    }


    public long getTime() {
        return time;
    }


    public double getLongitude() {
        return longitude;
    }


    public double getLatitude() {
        return latitude;
    }


    public double getDepth() {
        return depth;
    }


    public boolean isTsunami() {
        return tsunami;
    }


    public String getAlert() {
        return alert;
    }


    public String getStatus() {
        return status;
    }


    @Override
    public String toString() {

        return "EarthquakeData{" +
                "id='" + id + '\'' +
                ", magnitude=" + magnitude +
                ", place='" + place + '\'' +
                ", time=" + time +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", depth=" + depth +
                ", tsunami=" + tsunami +
                ", alert='" + alert + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
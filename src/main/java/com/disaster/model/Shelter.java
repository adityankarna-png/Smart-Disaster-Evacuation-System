package com.disaster.model;

public class Shelter {

    private int shelterId;
    private String name;
    private String location;
    private int capacity;
    private int currentOccupancy;
    private String status;

    public Shelter() {
    }

    public Shelter(int shelterId, String name, String location,
                   int capacity, int currentOccupancy, String status) {
        this.shelterId = shelterId;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.currentOccupancy = currentOccupancy;
        this.status = status;
    }

    public int getShelterId() {
        return shelterId;
    }

    public void setShelterId(int shelterId) {
        this.shelterId = shelterId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCurrentOccupancy() {
        return currentOccupancy;
    }

    public void setCurrentOccupancy(int currentOccupancy) {
        this.currentOccupancy = currentOccupancy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Shelter{" +
                "shelterId=" + shelterId +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", capacity=" + capacity +
                ", currentOccupancy=" + currentOccupancy +
                ", status='" + status + '\'' +
                '}';
    }
}
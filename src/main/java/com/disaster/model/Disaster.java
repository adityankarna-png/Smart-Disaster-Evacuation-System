package com.disaster.model;

public class Disaster {

    private int disasterId;
    private String type;
    private String location;
    private int severity;
    private String description;
    private String status;

    public Disaster() {
    }

    public Disaster(int disasterId, String type, String location,
                    int severity, String description, String status) {
        this.disasterId = disasterId;
        this.type = type;
        this.location = location;
        this.severity = severity;
        this.description = description;
        this.status = status;
    }

    public int getDisasterId() {
        return disasterId;
    }

    public void setDisasterId(int disasterId) {
        this.disasterId = disasterId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Disaster{" +
                "disasterId=" + disasterId +
                ", type='" + type + '\'' +
                ", location='" + location + '\'' +
                ", severity=" + severity +
                ", status='" + status + '\'' +
                '}';
    }
}
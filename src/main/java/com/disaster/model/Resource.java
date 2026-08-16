package com.disaster.model;

public class Resource {

    private int resourceId;
    private String resourceType;
    private String name;
    private int quantity;
    private String location;
    private String status;

    public Resource() {
    }

    public Resource(int resourceId, String resourceType, String name,
                    int quantity, String location, String status) {
        this.resourceId = resourceId;
        this.resourceType = resourceType;
        this.name = name;
        this.quantity = quantity;
        this.location = location;
        this.status = status;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "resourceId=" + resourceId +
                ", resourceType='" + resourceType + '\'' +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
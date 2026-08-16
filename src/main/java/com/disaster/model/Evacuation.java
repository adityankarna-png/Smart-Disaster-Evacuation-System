package com.disaster.model;

import java.time.LocalDateTime;

public class Evacuation {

    private int evacuationId;
    private int victimId;
    private int shelterId;
    private int resourceId;
    private int priorityScore;
    private String status;
    private LocalDateTime evacuationTime;

    public Evacuation() {
    }

    public Evacuation(int evacuationId, int victimId, int shelterId,
                      int resourceId, int priorityScore,
                      String status, LocalDateTime evacuationTime) {
        this.evacuationId = evacuationId;
        this.victimId = victimId;
        this.shelterId = shelterId;
        this.resourceId = resourceId;
        this.priorityScore = priorityScore;
        this.status = status;
        this.evacuationTime = evacuationTime;
    }

    public int getEvacuationId() {
        return evacuationId;
    }

    public void setEvacuationId(int evacuationId) {
        this.evacuationId = evacuationId;
    }

    public int getVictimId() {
        return victimId;
    }

    public void setVictimId(int victimId) {
        this.victimId = victimId;
    }

    public int getShelterId() {
        return shelterId;
    }

    public void setShelterId(int shelterId) {
        this.shelterId = shelterId;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public int getPriorityScore() {
        return priorityScore;
    }

    public void setPriorityScore(int priorityScore) {
        this.priorityScore = priorityScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getEvacuationTime() {
        return evacuationTime;
    }

    public void setEvacuationTime(LocalDateTime evacuationTime) {
        this.evacuationTime = evacuationTime;
    }

    @Override
    public String toString() {
        return "Evacuation{" +
                "evacuationId=" + evacuationId +
                ", victimId=" + victimId +
                ", shelterId=" + shelterId +
                ", resourceId=" + resourceId +
                ", priorityScore=" + priorityScore +
                ", status='" + status + '\'' +
                ", evacuationTime=" + evacuationTime +
                '}';
    }
}
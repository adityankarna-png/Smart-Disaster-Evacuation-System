package com.disaster.model;

public class Victim {

    private int victimId;
    private int disasterId;
    private String name;
    private int age;
    private String medicalCondition;
    private int vulnerabilityScore;
    private int zoneRisk;
    private int waitingTime;
    private int priorityScore;
    private String status;

    // Default constructor
    public Victim() {
    }

    // Parameterized constructor
    public Victim(int victimId, int disasterId, String name, int age,
                  String medicalCondition, int vulnerabilityScore,
                  int zoneRisk, int waitingTime, int priorityScore,
                  String status) {

        this.victimId = victimId;
        this.disasterId = disasterId;
        this.name = name;
        this.age = age;
        this.medicalCondition = medicalCondition;
        this.vulnerabilityScore = vulnerabilityScore;
        this.zoneRisk = zoneRisk;
        this.waitingTime = waitingTime;
        this.priorityScore = priorityScore;
        this.status = status;
    }

    public int getVictimId() {
        return victimId;
    }

    public void setVictimId(int victimId) {
        this.victimId = victimId;
    }

    public int getDisasterId() {
        return disasterId;
    }

    public void setDisasterId(int disasterId) {
        this.disasterId = disasterId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getMedicalCondition() {
        return medicalCondition;
    }

    public void setMedicalCondition(String medicalCondition) {
        this.medicalCondition = medicalCondition;
    }

    public int getVulnerabilityScore() {
        return vulnerabilityScore;
    }

    public void setVulnerabilityScore(int vulnerabilityScore) {
        this.vulnerabilityScore = vulnerabilityScore;
    }

    public int getZoneRisk() {
        return zoneRisk;
    }

    public void setZoneRisk(int zoneRisk) {
        this.zoneRisk = zoneRisk;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
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

    @Override
    public String toString() {
        return "Victim{" +
                "victimId=" + victimId +
                ", disasterId=" + disasterId +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", medicalCondition='" + medicalCondition + '\'' +
                ", vulnerabilityScore=" + vulnerabilityScore +
                ", zoneRisk=" + zoneRisk +
                ", waitingTime=" + waitingTime +
                ", priorityScore=" + priorityScore +
                ", status='" + status + '\'' +
                '}';
    }
}
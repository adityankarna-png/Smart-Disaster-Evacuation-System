package com.disaster.service;

import com.disaster.dao.EvacuationDAO;
import com.disaster.dao.ResourceDAO;
import com.disaster.dao.ShelterDAO;
import com.disaster.dao.VictimDAO;

import com.disaster.exceptions.NoResourceAvailableException;
import com.disaster.exceptions.NoShelterAvailableException;
import com.disaster.exceptions.VictimNotFoundException;

import com.disaster.model.Evacuation;
import com.disaster.model.Resource;
import com.disaster.model.Shelter;
import com.disaster.model.Victim;

import java.time.LocalDateTime;
import java.util.List;

public class EvacuationServiceImpl implements EvacuationService {

    private final VictimDAO victimDAO;
    private final ShelterDAO shelterDAO;
    private final ResourceDAO resourceDAO;
    private final EvacuationDAO evacuationDAO;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public EvacuationServiceImpl() {

        victimDAO = new VictimDAO();
        shelterDAO = new ShelterDAO();
        resourceDAO = new ResourceDAO();
        evacuationDAO = new EvacuationDAO();
    }

    // ==========================================
    // CALCULATE PRIORITY
    // ==========================================

    @Override
    public int calculatePriority(Victim victim) {

        int priority = 0;

        // Vulnerability score
        priority += victim.getVulnerabilityScore();

        // Zone risk
        priority += victim.getZoneRisk();

        // Waiting time
        priority += victim.getWaitingTime();

        // Medical condition
        if (victim.getMedicalCondition() != null
                && !victim.getMedicalCondition()
                .equalsIgnoreCase("None")) {

            priority += 5;
        }

        return priority;
    }

    // ==========================================
    // CREATE EVACUATION
    // ==========================================

    @Override
    public Evacuation createEvacuation(int victimId)
            throws VictimNotFoundException,
            NoShelterAvailableException,
            NoResourceAvailableException {

        // ==========================================
        // FIND VICTIM
        // ==========================================

        List<Victim> victims = victimDAO.getAllVictims();

        Victim selectedVictim = null;

        for (Victim victim : victims) {

            if (victim.getVictimId() == victimId) {

                selectedVictim = victim;
                break;
            }
        }

        // ==========================================
        // VICTIM NOT FOUND
        // ==========================================

        if (selectedVictim == null) {

            throw new VictimNotFoundException(
                    "Victim with ID " +
                            victimId +
                            " was not found."
            );
        }

        // ==========================================
        // CALCULATE PRIORITY
        // ==========================================

        int priority = calculatePriority(selectedVictim);

        selectedVictim.setPriorityScore(priority);

        System.out.println(
                "Calculated priority for " +
                        selectedVictim.getName() +
                        ": " +
                        priority
        );

        // ==========================================
        // FIND AVAILABLE SHELTER
        // ==========================================

        List<Shelter> shelters =
                shelterDAO.getAllShelters();

        Shelter selectedShelter = null;

        for (Shelter shelter : shelters) {

            if (shelter.getStatus()
                    .equalsIgnoreCase("AVAILABLE")
                    &&
                    shelter.getCurrentOccupancy()
                            < shelter.getCapacity()) {

                selectedShelter = shelter;
                break;
            }
        }

        // ==========================================
        // NO SHELTER AVAILABLE
        // ==========================================

        if (selectedShelter == null) {

            throw new NoShelterAvailableException(
                    "No available shelter found for victim ID "
                            + victimId
            );
        }

        // ==========================================
        // FIND AVAILABLE RESOURCE
        // ==========================================

        List<Resource> resources =
                resourceDAO.getAllResources();

        Resource selectedResource = null;

        for (Resource resource : resources) {

            if (resource.getStatus()
                    .equalsIgnoreCase("AVAILABLE")
                    &&
                    resource.getQuantity() > 0) {

                selectedResource = resource;
                break;
            }
        }

        // ==========================================
        // NO RESOURCE AVAILABLE
        // ==========================================

        if (selectedResource == null) {

            throw new NoResourceAvailableException(
                    "No available resource found for victim ID "
                            + victimId
            );
        }

        // ==========================================
        // CREATE EVACUATION
        // ==========================================

        Evacuation evacuation =
                new Evacuation();

        evacuation.setVictimId(
                selectedVictim.getVictimId()
        );

        evacuation.setShelterId(
                selectedShelter.getShelterId()
        );

        evacuation.setResourceId(
                selectedResource.getResourceId()
        );

        evacuation.setPriorityScore(
                priority
        );

        evacuation.setStatus(
                "PENDING"
        );

        evacuation.setEvacuationTime(
                LocalDateTime.now()
        );

        // ==========================================
        // SAVE EVACUATION
        // ==========================================

        evacuationDAO.addEvacuation(
                evacuation
        );

        // ==========================================
        // UPDATE SHELTER OCCUPANCY
        // ==========================================

        selectedShelter.setCurrentOccupancy(
                selectedShelter.getCurrentOccupancy() + 1
        );

        // If shelter reaches capacity,
        // change status to FULL
        if (selectedShelter.getCurrentOccupancy()
                >= selectedShelter.getCapacity()) {

            selectedShelter.setStatus("FULL");
        }

        shelterDAO.updateShelter(selectedShelter);

        // ==========================================
        // UPDATE RESOURCE QUANTITY
        // ==========================================

        selectedResource.setQuantity(
                selectedResource.getQuantity() - 1
        );

        // If resource quantity reaches 0,
        // change status to OUT_OF_STOCK
        if (selectedResource.getQuantity() <= 0) {

            selectedResource.setStatus("OUT_OF_STOCK");
        }

        resourceDAO.updateResource(selectedResource);

        // ==========================================
        // DISPLAY ASSIGNMENT
        // ==========================================

        System.out.println();

        System.out.println(
                "===== SMART EVACUATION ASSIGNMENT ====="
        );

        System.out.println(
                "Victim     : " +
                        selectedVictim.getName()
        );

        System.out.println(
                "Priority   : " +
                        priority
        );

        System.out.println(
                "Shelter    : " +
                        selectedShelter.getName()
        );

        System.out.println(
                "Resource   : " +
                        selectedResource.getName()
        );

        System.out.println(
                "Status     : PENDING"
        );

        System.out.println(
                "Shelter Occupancy : " +
                        selectedShelter.getCurrentOccupancy()
                        + "/" +
                        selectedShelter.getCapacity()
        );

        System.out.println(
                "Resource Quantity : " +
                        selectedResource.getQuantity()
        );

        return evacuation;
    }

    // ==========================================
// EMERGENCY EVACUATION
// ==========================================

    public Evacuation createEmergencyEvacuation()
            throws VictimNotFoundException,
            NoShelterAvailableException,
            NoResourceAvailableException {

        List<Victim> victims =
                victimDAO.getAllVictims();

        if (victims == null || victims.isEmpty()) {

            throw new VictimNotFoundException(
                    "No victims are currently registered."
            );
        }

        // ==========================================
        // FIND HIGHEST PRIORITY VICTIM
        // ==========================================

        Victim highestPriorityVictim = null;

        int highestPriority = Integer.MIN_VALUE;

        for (Victim victim : victims) {

            int priority =
                    calculatePriority(victim);

            victim.setPriorityScore(priority);

            if (priority > highestPriority) {

                highestPriority = priority;

                highestPriorityVictim = victim;
            }
        }

        // ==========================================
        // SAFETY CHECK
        // ==========================================

        if (highestPriorityVictim == null) {

            throw new VictimNotFoundException(
                    "Unable to determine an emergency evacuation victim."
            );
        }

        System.out.println();
        System.out.println(
                "===== EMERGENCY EVACUATION TRIGGERED ====="
        );

        System.out.println(
                "Selected Victim : "
                        + highestPriorityVictim.getName()
        );

        System.out.println(
                "Priority        : "
                        + highestPriority
        );

        // ==========================================
        // CREATE NORMAL EVACUATION
        // ==========================================

        return createEvacuation(
                highestPriorityVictim.getVictimId()
        );
    }
}
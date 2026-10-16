package com.disaster.service;

import com.disaster.exceptions.NoResourceAvailableException;
import com.disaster.exceptions.NoShelterAvailableException;
import com.disaster.exceptions.VictimNotFoundException;
import com.disaster.model.Evacuation;
import com.disaster.model.Victim;

public interface EvacuationService {

    int calculatePriority(Victim victim);

    Evacuation createEvacuation(int victimId)
            throws VictimNotFoundException,
            NoShelterAvailableException,
            NoResourceAvailableException;

    Evacuation createEmergencyEvacuation()
            throws VictimNotFoundException,
            NoShelterAvailableException,
            NoResourceAvailableException;
}
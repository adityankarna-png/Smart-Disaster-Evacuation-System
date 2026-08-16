package com.disaster.database;

import com.disaster.exceptions.NoResourceAvailableException;
import com.disaster.exceptions.NoShelterAvailableException;
import com.disaster.exceptions.VictimNotFoundException;

import com.disaster.model.Evacuation;

import com.disaster.service.EvacuationService;
import com.disaster.service.EvacuationServiceImpl;

public class Main {

    public static void main(String[] args) {

        System.out.println(
                "======================================"
        );

        System.out.println(
                "   SMART DISASTER EVACUATION SYSTEM"
        );

        System.out.println(
                "======================================"
        );



        EvacuationService evacuationService =
                new EvacuationServiceImpl();



        try {

            // Use an actual victim ID
            Evacuation evacuation =
                    evacuationService.createEvacuation(4);

            if (evacuation != null) {

                System.out.println();

                System.out.println(
                        "Evacuation created successfully!"
                );
            }

        }


        catch (VictimNotFoundException e) {

            System.out.println();

            System.out.println(
                    "VICTIM ERROR: " +
                            e.getMessage()
            );
        }


        catch (NoShelterAvailableException e) {

            System.out.println();

            System.out.println(
                    "SHELTER ERROR: " +
                            e.getMessage()
            );
        }


        catch (NoResourceAvailableException e) {

            System.out.println();

            System.out.println(
                    "RESOURCE ERROR: " +
                            e.getMessage()
            );
        }


        catch (Exception e) {

            System.out.println();

            System.out.println(
                    "UNEXPECTED ERROR: " +
                            e.getMessage()
            );

            e.printStackTrace();
        }
    }
}
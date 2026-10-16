package com.disaster.earthquake;

import com.disaster.service.NotificationServiceThread;
import com.disaster.service.VictimGenerationService;
import com.disaster.model.Victim;
import com.disaster.service.EvacuationServiceImpl;
import com.disaster.model.Evacuation;

import java.util.List;

public class EarthquakeNotificationTest {

    public static void main(String[] args) {

        System.out.println();
        System.out.println(
                "=========================================="
        );
        System.out.println(
                "     EARTHQUAKE NOTIFICATION TEST"
        );
        System.out.println(
                "=========================================="
        );


        // ==========================================
        // CREATE NOTIFICATION SERVICE
        // ==========================================

        NotificationServiceThread
                notificationService =
                new NotificationServiceThread();


        // ==========================================
        // CREATE TEST EARTHQUAKE
        // ==========================================

        EarthquakeData testEarthquake =
                new EarthquakeData(

                        "TEST-EARTHQUAKE-001",

                        5.7,

                        "120 km southwest of Chennai",

                        System.currentTimeMillis(),

                        79.8500,

                        12.4500,

                        18.5,

                        false,

                        "green",

                        "reviewed"
                );

        // ==========================================
        // AUTOMATIC VICTIM GENERATION
        // ==========================================

        VictimGenerationService victimGenerationService =
                new VictimGenerationService();

        List<Victim> generatedVictims =
                victimGenerationService.generateVictims(
                        5
                );

        System.out.println(
                "[TEST] Automatically generated and saved "
                        + generatedVictims.size()
                        + " victims."
        );

        // ==========================================
// SELECT HIGHEST PRIORITY VICTIM
// ==========================================

        Victim highestPriorityVictim = null;

        for (Victim victim : generatedVictims) {

            if (highestPriorityVictim == null
                    || victim.getPriorityScore()
                    > highestPriorityVictim.getPriorityScore()) {

                highestPriorityVictim = victim;
            }
        }


        if (highestPriorityVictim != null) {

            System.out.println();

            System.out.println(
                    "=========================================="
            );

            System.out.println(
                    "     HIGHEST PRIORITY VICTIM"
            );

            System.out.println(
                    "=========================================="
            );

            System.out.println(
                    "Victim   : "
                            + highestPriorityVictim.getName()
            );

            System.out.println(
                    "Victim ID: "
                            + highestPriorityVictim.getVictimId()
            );

            System.out.println(
                    "Priority : "
                            + highestPriorityVictim.getPriorityScore()
            );

            System.out.println(
                    "Medical  : "
                            + highestPriorityVictim.getMedicalCondition()
            );

            System.out.println(
                    "=========================================="
            );


            // ==========================================
            // CREATE EVACUATION
            // ==========================================

            try {

                EvacuationServiceImpl
                        evacuationService =
                        new EvacuationServiceImpl();

                Evacuation evacuation =
                        evacuationService.createEvacuation(
                                highestPriorityVictim.getVictimId()
                        );

                System.out.println();

                System.out.println(
                        "[TEST] Evacuation created successfully."
                );

                System.out.println(
                        "[TEST] Evacuation ID: "
                                + evacuation.getEvacuationId()
                );

            } catch (Exception e) {

                System.err.println(
                        "[TEST] Evacuation creation failed: "
                                + e.getMessage()
                );

                e.printStackTrace();
            }
        }


        // ==========================================
        // SEND NOTIFICATION
        // ==========================================

        System.out.println();
        System.out.println(
                "Sending test earthquake..."
        );


        notificationService.notifyEarthquake(
                testEarthquake
        );


        // ==========================================
        // COMPLETE
        // ==========================================

        System.out.println();
        System.out.println(
                "=========================================="
        );
        System.out.println(
                "   EARTHQUAKE NOTIFICATION TEST COMPLETE"
        );
        System.out.println(
                "=========================================="
        );
    }
}
package com.disaster.service;

import com.disaster.dao.VictimDAO;
import com.disaster.database.DBConnection;
import com.disaster.model.Victim;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VictimGenerationService {

    private final VictimDAO victimDAO;
    private final Random random;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public VictimGenerationService() {

        victimDAO = new VictimDAO();

        random = new Random();
    }


    // ==========================================
    // GENERATE VICTIMS FOR LATEST DISASTER
    // ==========================================

    public List<Victim> generateVictims(
            int numberOfVictims
    ) {

        int disasterId =
                getLatestDisasterId();

        if (disasterId == -1) {

            System.out.println(
                    "[Victim Generation] No disaster found."
            );

            System.out.println(
                    "[Victim Generation] Victims were not generated."
            );

            return new ArrayList<>();
        }

        return generateVictims(
                disasterId,
                numberOfVictims
        );
    }


    // ==========================================
    // GENERATE VICTIMS
    // ==========================================

    public List<Victim> generateVictims(
            int disasterId,
            int numberOfVictims
    ) {

        List<Victim> generatedVictims =
                new ArrayList<>();


        if (numberOfVictims <= 0) {

            return generatedVictims;
        }


        // ==========================================
        // VERIFY DISASTER EXISTS
        // ==========================================

        if (!disasterExists(disasterId)) {

            System.out.println(
                    "[Victim Generation] ERROR: Disaster ID "
                            + disasterId
                            + " does not exist."
            );

            return generatedVictims;
        }


        System.out.println();

        System.out.println(
                "=========================================="
        );

        System.out.println(
                "       AUTOMATIC VICTIM GENERATION"
        );

        System.out.println(
                "=========================================="
        );

        System.out.println(
                "Disaster ID : "
                        + disasterId
        );

        System.out.println(
                "Generating  : "
                        + numberOfVictims
                        + " simulated victims"
        );


        // ==========================================
        // GENERATE
        // ==========================================

        for (int i = 1;
             i <= numberOfVictims;
             i++) {

            Victim victim =
                    createVictim(
                            disasterId,
                            i
                    );


            try {

                int victimId =
                        victimDAO.addVictim(
                                victim
                        );

                if (victimId != -1) {

                    generatedVictims.add(
                            victim
                    );

                    System.out.println(
                            "[Victim Generated] "
                                    + victim.getName()
                                    + " | ID: "
                                    + victim.getVictimId()
                                    + " | Age: "
                                    + victim.getAge()
                                    + " | Vulnerability: "
                                    + victim.getVulnerabilityScore()
                                    + " | Zone Risk: "
                                    + victim.getZoneRisk()
                                    + " | Waiting: "
                                    + victim.getWaitingTime()
                                    + " | Medical: "
                                    + victim.getMedicalCondition()
                                    + " | Priority: "
                                    + victim.getPriorityScore()
                    );
                }

                System.out.println(
                        "[Victim Generated] "
                                + victim.getName()
                                + " | Age: "
                                + victim.getAge()
                                + " | Vulnerability: "
                                + victim.getVulnerabilityScore()
                                + " | Zone Risk: "
                                + victim.getZoneRisk()
                                + " | Waiting: "
                                + victim.getWaitingTime()
                                + " | Medical: "
                                + victim.getMedicalCondition()
                                + " | Priority: "
                                + victim.getPriorityScore()
                );

            } catch (Exception e) {

                System.err.println(
                        "[Victim Generation] Failed to add "
                                + victim.getName()
                );

                e.printStackTrace();
            }
        }


        System.out.println();

        System.out.println(
                "Successfully generated and saved: "
                        + generatedVictims.size()
                        + " / "
                        + numberOfVictims
        );

        System.out.println(
                "=========================================="
        );

        System.out.println();


        return generatedVictims;
    }


    // ==========================================
    // GET LATEST DISASTER ID
    // ==========================================

    private int getLatestDisasterId() {

        String sql =
                "SELECT disaster_id " +
                        "FROM disasters " +
                        "ORDER BY disaster_id DESC " +
                        "LIMIT 1";


        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            if (resultSet.next()) {

                return resultSet.getInt(
                        "disaster_id"
                );
            }

        } catch (Exception e) {

            System.err.println(
                    "[Victim Generation] " +
                            "Could not find latest disaster."
            );

            e.printStackTrace();
        }


        return -1;
    }


    // ==========================================
    // CHECK DISASTER
    // ==========================================

    private boolean disasterExists(
            int disasterId
    ) {

        String sql =
                "SELECT COUNT(*) " +
                        "FROM disasters " +
                        "WHERE disaster_id = ?";


        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    disasterId
            );


            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {

                    return resultSet.getInt(1) > 0;
                }
            }

        } catch (Exception e) {

            System.err.println(
                    "[Victim Generation] " +
                            "Could not verify disaster."
            );

            e.printStackTrace();
        }


        return false;
    }


    // ==========================================
    // CREATE VICTIM
    // ==========================================

    private Victim createVictim(
            int disasterId,
            int number
    ) {

        Victim victim =
                new Victim();


        victim.setDisasterId(
                disasterId
        );


        victim.setName(
                "Simulated Victim "
                        + number
        );


        victim.setAge(
                random.nextInt(81) + 5
        );


        victim.setMedicalCondition(
                generateMedicalCondition()
        );


        victim.setVulnerabilityScore(
                random.nextInt(10) + 1
        );


        victim.setZoneRisk(
                random.nextInt(10) + 1
        );


        victim.setWaitingTime(
                random.nextInt(11)
        );


        // ==========================================
        // PRIORITY
        // ==========================================

        int priority =
                victim.getVulnerabilityScore()
                        +
                        victim.getZoneRisk()
                        +
                        victim.getWaitingTime();


        if (!victim.getMedicalCondition()
                .equalsIgnoreCase("None")) {

            priority += 5;
        }


        victim.setPriorityScore(
                priority
        );


        victim.setStatus(
                "WAITING"
        );


        return victim;
    }


    // ==========================================
    // MEDICAL CONDITION
    // ==========================================

    private String generateMedicalCondition() {

        int value =
                random.nextInt(100);


        if (value < 60) {

            return "None";

        } else if (value < 75) {

            return "Injury";

        } else if (value < 85) {

            return "Fracture";

        } else if (value < 93) {

            return "Asthma";

        } else {

            return "Heart Condition";
        }
    }
}
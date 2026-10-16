package com.disaster.dao;

import com.disaster.database.DBConnection;
import com.disaster.model.Victim;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class VictimDAO {

    public int addVictim(Victim victim) {

        String sql = "INSERT INTO victims " +
                "(disaster_id, name, age, medical_condition, vulnerability_score, " +
                "zone_risk, waiting_time, priority_score, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                sql,
                                java.sql.Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            statement.setInt(
                    1,
                    victim.getDisasterId()
            );

            statement.setString(
                    2,
                    victim.getName()
            );

            statement.setInt(
                    3,
                    victim.getAge()
            );

            statement.setString(
                    4,
                    victim.getMedicalCondition()
            );

            statement.setInt(
                    5,
                    victim.getVulnerabilityScore()
            );

            statement.setInt(
                    6,
                    victim.getZoneRisk()
            );

            statement.setInt(
                    7,
                    victim.getWaitingTime()
            );

            statement.setInt(
                    8,
                    victim.getPriorityScore()
            );

            statement.setString(
                    9,
                    victim.getStatus()
            );


            int rowsInserted =
                    statement.executeUpdate();


            if (rowsInserted > 0) {

                try (
                        ResultSet generatedKeys =
                                statement.getGeneratedKeys()
                ) {

                    if (generatedKeys.next()) {

                        int generatedId =
                                generatedKeys.getInt(1);

                        victim.setVictimId(
                                generatedId
                        );

                        System.out.println(
                                "Victim added successfully! "
                                        + "ID: "
                                        + generatedId
                        );

                        return generatedId;
                    }
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Error adding victim."
            );

            e.printStackTrace();
        }


        return -1;
    }

    public List<Victim> getAllVictims() {

        List<Victim> victims = new ArrayList<>();

        String sql = "SELECT * FROM victims";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                Victim victim = new Victim();

                victim.setVictimId(resultSet.getInt("victim_id"));
                victim.setDisasterId(resultSet.getInt("disaster_id"));
                victim.setName(resultSet.getString("name"));
                victim.setAge(resultSet.getInt("age"));
                victim.setMedicalCondition(resultSet.getString("medical_condition"));
                victim.setVulnerabilityScore(resultSet.getInt("vulnerability_score"));
                victim.setZoneRisk(resultSet.getInt("zone_risk"));
                victim.setWaitingTime(resultSet.getInt("waiting_time"));
                victim.setPriorityScore(resultSet.getInt("priority_score"));
                victim.setStatus(resultSet.getString("status"));

                victims.add(victim);
            }
        } catch (Exception e) {
            System.out.println("Error retrieving victims.");
            e.printStackTrace();
        }

        return victims;
    }

    // ==========================================
// DELETE VICTIM
// ==========================================

    public void deleteVictim(int victimId) {

        String deleteEvacuations =
                "DELETE FROM evacuations WHERE victim_id = ?";

        String deleteVictim =
                "DELETE FROM victims WHERE victim_id = ?";

        try (Connection connection = DBConnection.getConnection()) {

            // Start transaction
            connection.setAutoCommit(false);

            try (
                    PreparedStatement evacuationStatement =
                            connection.prepareStatement(deleteEvacuations);

                    PreparedStatement victimStatement =
                            connection.prepareStatement(deleteVictim)
            ) {

                // ==========================================
                // FIRST DELETE EVACUATION RECORDS
                // ==========================================

                evacuationStatement.setInt(1, victimId);

                evacuationStatement.executeUpdate();


                // ==========================================
                // THEN DELETE VICTIM
                // ==========================================

                victimStatement.setInt(1, victimId);

                int rowsDeleted =
                        victimStatement.executeUpdate();


                // ==========================================
                // COMMIT
                // ==========================================

                connection.commit();

                if (rowsDeleted > 0) {

                    System.out.println(
                            "Victim deleted successfully!"
                    );

                } else {

                    System.out.println(
                            "Victim not found."
                    );
                }

            } catch (Exception e) {

                // Roll back if anything fails
                connection.rollback();

                System.out.println(
                        "Error deleting victim."
                );

                e.printStackTrace();
            }

        } catch (Exception e) {

            System.out.println(
                    "Database connection error while deleting victim."
            );

            e.printStackTrace();
        }
    }

    public void updateVictim(Victim victim) {

        String sql = "UPDATE victims SET " +
                "disaster_id = ?, " +
                "name = ?, " +
                "age = ?, " +
                "medical_condition = ?, " +
                "vulnerability_score = ?, " +
                "zone_risk = ?, " +
                "waiting_time = ?, " +
                "priority_score = ?, " +
                "status = ? " +
                "WHERE victim_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    victim.getDisasterId()
            );

            statement.setString(
                    2,
                    victim.getName()
            );

            statement.setInt(
                    3,
                    victim.getAge()
            );

            statement.setString(
                    4,
                    victim.getMedicalCondition()
            );

            statement.setInt(
                    5,
                    victim.getVulnerabilityScore()
            );

            statement.setInt(
                    6,
                    victim.getZoneRisk()
            );

            statement.setInt(
                    7,
                    victim.getWaitingTime()
            );

            statement.setInt(
                    8,
                    victim.getPriorityScore()
            );

            statement.setString(
                    9,
                    victim.getStatus()
            );

            statement.setInt(
                    10,
                    victim.getVictimId()
            );

            statement.executeUpdate();

            System.out.println(
                    "Victim updated successfully!"
            );

        } catch (Exception e) {

            System.out.println(
                    "Error updating victim."
            );

            e.printStackTrace();
        }
    }
}
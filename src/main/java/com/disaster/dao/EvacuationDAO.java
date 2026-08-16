package com.disaster.dao;

import com.disaster.database.DBConnection;
import com.disaster.model.Evacuation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EvacuationDAO {

    public void addEvacuation(Evacuation evacuation) {

        String sql = "INSERT INTO evacuations " +
                "(victim_id, shelter_id, resource_id, priority_score, status, evacuation_time) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, evacuation.getVictimId());
            statement.setInt(2, evacuation.getShelterId());
            statement.setInt(3, evacuation.getResourceId());
            statement.setInt(4, evacuation.getPriorityScore());
            statement.setString(5, evacuation.getStatus());

            if (evacuation.getEvacuationTime() != null) {
                statement.setTimestamp(
                        6,
                        java.sql.Timestamp.valueOf(evacuation.getEvacuationTime())
                );
            } else {
                statement.setTimestamp(6, null);
            }

            statement.executeUpdate();

            System.out.println("Evacuation added successfully!");

        } catch (Exception e) {
            System.out.println("Error adding evacuation.");
            e.printStackTrace();
        }
    }

    public List<Evacuation> getAllEvacuations() {

        List<Evacuation> evacuations = new ArrayList<>();

        String sql = "SELECT * FROM evacuations";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                Evacuation evacuation = new Evacuation();

                evacuation.setEvacuationId(
                        resultSet.getInt("evacuation_id")
                );

                evacuation.setVictimId(
                        resultSet.getInt("victim_id")
                );

                evacuation.setShelterId(
                        resultSet.getInt("shelter_id")
                );

                evacuation.setResourceId(
                        resultSet.getInt("resource_id")
                );

                evacuation.setPriorityScore(
                        resultSet.getInt("priority_score")
                );

                evacuation.setStatus(
                        resultSet.getString("status")
                );

                if (resultSet.getTimestamp("evacuation_time") != null) {
                    evacuation.setEvacuationTime(
                            resultSet.getTimestamp("evacuation_time")
                                    .toLocalDateTime()
                    );
                }

                evacuations.add(evacuation);
            }

        } catch (Exception e) {
            System.out.println("Error retrieving evacuations.");
            e.printStackTrace();
        }

        return evacuations;
    }

    public void updateEvacuation(Evacuation evacuation) {

        String sql = "UPDATE evacuations SET " +
                "victim_id = ?, " +
                "shelter_id = ?, " +
                "resource_id = ?, " +
                "priority_score = ?, " +
                "status = ?, " +
                "evacuation_time = ? " +
                "WHERE evacuation_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, evacuation.getVictimId());
            statement.setInt(2, evacuation.getShelterId());
            statement.setInt(3, evacuation.getResourceId());
            statement.setInt(4, evacuation.getPriorityScore());
            statement.setString(5, evacuation.getStatus());

            if (evacuation.getEvacuationTime() != null) {
                statement.setTimestamp(
                        6,
                        java.sql.Timestamp.valueOf(
                                evacuation.getEvacuationTime()
                        )
                );
            } else {
                statement.setTimestamp(6, null);
            }

            statement.setInt(
                    7,
                    evacuation.getEvacuationId()
            );

            statement.executeUpdate();

            System.out.println("Evacuation updated successfully!");

        } catch (Exception e) {
            System.out.println("Error updating evacuation.");
            e.printStackTrace();
        }
    }

    public void deleteEvacuation(int evacuationId) {

        String sql =
                "DELETE FROM evacuations WHERE evacuation_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, evacuationId);

            statement.executeUpdate();

            System.out.println("Evacuation deleted successfully!");

        } catch (Exception e) {
            System.out.println("Error deleting evacuation.");
            e.printStackTrace();
        }
    }
}
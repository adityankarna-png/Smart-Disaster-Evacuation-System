package com.disaster.dao;

import com.disaster.database.DBConnection;
import com.disaster.model.Disaster;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DisasterDAO {

    public void addDisaster(Disaster disaster) {

        String sql = "INSERT INTO disasters " +
                "(type, location, severity, description, status) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, disaster.getType());
            statement.setString(2, disaster.getLocation());
            statement.setInt(3, disaster.getSeverity());
            statement.setString(4, disaster.getDescription());
            statement.setString(5, disaster.getStatus());

            statement.executeUpdate();

            System.out.println("Disaster added successfully!");

        } catch (Exception e) {
            System.out.println("Error adding disaster.");
            e.printStackTrace();
        }
    }

    public List<Disaster> getAllDisasters() {

        List<Disaster> disasters = new ArrayList<>();

        String sql = "SELECT * FROM disasters";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                Disaster disaster = new Disaster();

                disaster.setDisasterId(
                        resultSet.getInt("disaster_id"));

                disaster.setType(
                        resultSet.getString("type"));

                disaster.setLocation(
                        resultSet.getString("location"));

                disaster.setSeverity(
                        resultSet.getInt("severity"));

                disaster.setDescription(
                        resultSet.getString("description"));

                disaster.setStatus(
                        resultSet.getString("status"));

                disasters.add(disaster);
            }

        } catch (Exception e) {
            System.out.println("Error retrieving disasters.");
            e.printStackTrace();
        }

        return disasters;
    }

    public void deleteDisaster(int disasterId) {

        String sql = "DELETE FROM disasters WHERE disaster_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, disasterId);

            statement.executeUpdate();

            System.out.println("Disaster deleted successfully!");

        } catch (Exception e) {
            System.out.println("Error deleting disaster.");
            e.printStackTrace();
        }
    }

    public void updateDisaster(Disaster disaster) {

        String sql = "UPDATE disasters SET " +
                "type = ?, " +
                "location = ?, " +
                "severity = ?, " +
                "description = ?, " +
                "status = ? " +
                "WHERE disaster_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    disaster.getType()
            );

            statement.setString(
                    2,
                    disaster.getLocation()
            );

            statement.setInt(
                    3,
                    disaster.getSeverity()
            );

            statement.setString(
                    4,
                    disaster.getDescription()
            );

            statement.setString(
                    5,
                    disaster.getStatus()
            );

            statement.setInt(
                    6,
                    disaster.getDisasterId()
            );

            statement.executeUpdate();

            System.out.println(
                    "Disaster updated successfully!"
            );

        } catch (Exception e) {

            System.out.println(
                    "Error updating disaster."
            );

            e.printStackTrace();
        }
    }
}
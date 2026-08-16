package com.disaster.dao;

import com.disaster.database.DBConnection;
import com.disaster.model.Shelter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ShelterDAO {


    public void addShelter(Shelter shelter) {

        String sql = "INSERT INTO shelters " +
                "(name, location, capacity, current_occupancy, status) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, shelter.getName());
            statement.setString(2, shelter.getLocation());
            statement.setInt(3, shelter.getCapacity());
            statement.setInt(4, shelter.getCurrentOccupancy());
            statement.setString(5, shelter.getStatus());

            statement.executeUpdate();

            System.out.println("Shelter added successfully!");

        } catch (Exception e) {

            System.out.println("Error adding shelter.");
            e.printStackTrace();
        }
    }



    public List<Shelter> getAllShelters() {

        List<Shelter> shelters = new ArrayList<>();

        String sql = "SELECT * FROM shelters";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {

                Shelter shelter = new Shelter();

                shelter.setShelterId(
                        resultSet.getInt("shelter_id")
                );

                shelter.setName(
                        resultSet.getString("name")
                );

                shelter.setLocation(
                        resultSet.getString("location")
                );

                shelter.setCapacity(
                        resultSet.getInt("capacity")
                );

                shelter.setCurrentOccupancy(
                        resultSet.getInt("current_occupancy")
                );

                shelter.setStatus(
                        resultSet.getString("status")
                );

                shelters.add(shelter);
            }

        } catch (Exception e) {

            System.out.println("Error retrieving shelters.");
            e.printStackTrace();
        }

        return shelters;
    }



    public void updateShelter(Shelter shelter) {

        String sql = "UPDATE shelters SET " +
                "name = ?, " +
                "location = ?, " +
                "capacity = ?, " +
                "current_occupancy = ?, " +
                "status = ? " +
                "WHERE shelter_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, shelter.getName());

            statement.setString(2, shelter.getLocation());

            statement.setInt(3, shelter.getCapacity());

            statement.setInt(
                    4,
                    shelter.getCurrentOccupancy()
            );

            statement.setString(
                    5,
                    shelter.getStatus()
            );

            statement.setInt(
                    6,
                    shelter.getShelterId()
            );

            statement.executeUpdate();

            System.out.println("Shelter updated successfully!");

        } catch (Exception e) {

            System.out.println("Error updating shelter.");
            e.printStackTrace();
        }
    }



    public void deleteShelter(int shelterId) {

        String sql =
                "DELETE FROM shelters WHERE shelter_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, shelterId);

            statement.executeUpdate();

            System.out.println("Shelter deleted successfully!");

        } catch (Exception e) {

            System.out.println("Error deleting shelter.");
            e.printStackTrace();
        }
    }
}
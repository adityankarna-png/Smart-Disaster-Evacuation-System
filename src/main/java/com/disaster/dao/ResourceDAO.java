package com.disaster.dao;

import com.disaster.database.DBConnection;
import com.disaster.model.Resource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ResourceDAO {


    public void addResource(Resource resource) {

        String sql = "INSERT INTO resources " +
                "(resource_type, name, quantity, location, status) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    resource.getResourceType()
            );

            statement.setString(
                    2,
                    resource.getName()
            );

            statement.setInt(
                    3,
                    resource.getQuantity()
            );

            statement.setString(
                    4,
                    resource.getLocation()
            );

            statement.setString(
                    5,
                    resource.getStatus()
            );

            statement.executeUpdate();

            System.out.println("Resource added successfully!");

        } catch (Exception e) {

            System.out.println("Error adding resource.");
            e.printStackTrace();
        }
    }



    public List<Resource> getAllResources() {

        List<Resource> resources = new ArrayList<>();

        String sql = "SELECT * FROM resources";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {

                Resource resource = new Resource();

                resource.setResourceId(
                        resultSet.getInt("resource_id")
                );

                resource.setResourceType(
                        resultSet.getString("resource_type")
                );

                resource.setName(
                        resultSet.getString("name")
                );

                resource.setQuantity(
                        resultSet.getInt("quantity")
                );

                resource.setLocation(
                        resultSet.getString("location")
                );

                resource.setStatus(
                        resultSet.getString("status")
                );

                resources.add(resource);
            }

        } catch (Exception e) {

            System.out.println("Error retrieving resources.");
            e.printStackTrace();
        }

        return resources;
    }



    public void updateResource(Resource resource) {

        String sql = "UPDATE resources SET " +
                "resource_type = ?, " +
                "name = ?, " +
                "quantity = ?, " +
                "location = ?, " +
                "status = ? " +
                "WHERE resource_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    resource.getResourceType()
            );

            statement.setString(
                    2,
                    resource.getName()
            );

            statement.setInt(
                    3,
                    resource.getQuantity()
            );

            statement.setString(
                    4,
                    resource.getLocation()
            );

            statement.setString(
                    5,
                    resource.getStatus()
            );

            statement.setInt(
                    6,
                    resource.getResourceId()
            );

            statement.executeUpdate();

            System.out.println("Resource updated successfully!");

        } catch (Exception e) {

            System.out.println("Error updating resource.");
            e.printStackTrace();
        }
    }



    public void deleteResource(int resourceId) {

        String sql =
                "DELETE FROM resources WHERE resource_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, resourceId);

            statement.executeUpdate();

            System.out.println("Resource deleted successfully!");

        } catch (Exception e) {

            System.out.println("Error deleting resource.");
            e.printStackTrace();
        }
    }
}
package com.disaster.dao;

import com.disaster.database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReportDAO {

    // ==========================================
    // GENERIC COUNT
    // ==========================================

    private int getCount(String sql) {

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }

        } catch (Exception e) {

            System.out.println(
                    "Error getting report count."
            );

            e.printStackTrace();
        }

        return 0;
    }


    // ==========================================
    // DISASTERS
    // ==========================================

    public int getTotalDisasters() {

        return getCount(
                "SELECT COUNT(*) FROM disasters"
        );
    }


    public int getActiveDisasters() {

        return getCount(
                "SELECT COUNT(*) " +
                        "FROM disasters " +
                        "WHERE status = 'ACTIVE'"
        );
    }


    public int getResolvedDisasters() {

        return getCount(
                "SELECT COUNT(*) " +
                        "FROM disasters " +
                        "WHERE status = 'RESOLVED'"
        );
    }


    // ==========================================
    // VICTIMS
    // ==========================================

    public int getTotalVictims() {

        return getCount(
                "SELECT COUNT(*) FROM victims"
        );
    }


    public int getHighPriorityVictims() {

        return getCount(
                "SELECT COUNT(*) " +
                        "FROM victims " +
                        "WHERE priority_score >= 20"
        );
    }


    public int getWaitingVictims() {

        return getCount(
                "SELECT COUNT(*) " +
                        "FROM victims " +
                        "WHERE status = 'WAITING'"
        );
    }


    // ==========================================
    // SHELTERS
    // ==========================================

    public int getTotalShelters() {

        return getCount(
                "SELECT COUNT(*) FROM shelters"
        );
    }


    public int getAvailableShelters() {

        return getCount(
                "SELECT COUNT(*) " +
                        "FROM shelters " +
                        "WHERE status = 'AVAILABLE'"
        );
    }


    public int getFullShelters() {

        return getCount(
                "SELECT COUNT(*) " +
                        "FROM shelters " +
                        "WHERE status = 'FULL'"
        );
    }


    // ==========================================
    // RESOURCES
    // ==========================================

    public int getTotalResources() {

        return getCount(
                "SELECT COUNT(*) FROM resources"
        );
    }


    public int getAvailableResources() {

        return getCount(
                "SELECT COUNT(*) " +
                        "FROM resources " +
                        "WHERE status = 'AVAILABLE'"
        );
    }


    public int getOutOfStockResources() {

        return getCount(
                "SELECT COUNT(*) " +
                        "FROM resources " +
                        "WHERE status = 'OUT_OF_STOCK'"
        );
    }


    // ==========================================
    // EVACUATIONS
    // ==========================================

    public int getTotalEvacuations() {

        return getCount(
                "SELECT COUNT(*) FROM evacuations"
        );
    }


    public int getPendingEvacuations() {

        return getCount(
                "SELECT COUNT(*) " +
                        "FROM evacuations " +
                        "WHERE status = 'PENDING'"
        );
    }


    public int getCompletedEvacuations() {

        return getCount(
                "SELECT COUNT(*) " +
                        "FROM evacuations " +
                        "WHERE status = 'COMPLETED'"
        );
    }


    // ==========================================
    // SHELTER OCCUPANCY
    // ==========================================

    public int getTotalShelterCapacity() {

        return getCount(
                "SELECT COALESCE(SUM(capacity), 0) " +
                        "FROM shelters"
        );
    }


    public int getCurrentShelterOccupancy() {

        return getCount(
                "SELECT COALESCE(SUM(current_occupancy), 0) " +
                        "FROM shelters"
        );
    }


    // ==========================================
    // RESOURCE QUANTITY
    // ==========================================

    public int getTotalResourceQuantity() {

        return getCount(
                "SELECT COALESCE(SUM(quantity), 0) " +
                        "FROM resources"
        );
    }

    // ==========================================
// LATEST EVACUATION RESPONSE
// ==========================================

    public String getLatestEvacuationResponse() {

        String sql =
                "SELECT " +
                        "e.evacuation_id, " +
                        "e.victim_id, " +
                        "e.shelter_id, " +
                        "e.resource_id, " +
                        "e.priority_score, " +
                        "e.status, " +
                        "v.name AS victim_name, " +
                        "s.name AS shelter_name, " +
                        "r.name AS resource_name " +
                        "FROM evacuations e " +

                        "LEFT JOIN victims v " +
                        "ON e.victim_id = v.victim_id " +

                        "LEFT JOIN shelters s " +
                        "ON e.shelter_id = s.shelter_id " +

                        "LEFT JOIN resources r " +
                        "ON e.resource_id = r.resource_id " +

                        "ORDER BY e.evacuation_id DESC " +
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

                return
                        "Evacuation ID: "
                                + resultSet.getInt(
                                "evacuation_id"
                        )
                                + "\nVictim: "
                                + resultSet.getString(
                                "victim_name"
                        )
                                + "\nPriority: "
                                + resultSet.getInt(
                                "priority_score"
                        )
                                + "\nShelter: "
                                + resultSet.getString(
                                "shelter_name"
                        )
                                + "\nResource: "
                                + resultSet.getString(
                                "resource_name"
                        )
                                + "\nStatus: "
                                + resultSet.getString(
                                "status"
                        );
            }

        } catch (Exception e) {

            System.out.println(
                    "Error getting latest evacuation response."
            );

            e.printStackTrace();
        }

        return "No evacuation response available.";
    }
}
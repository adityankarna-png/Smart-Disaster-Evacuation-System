package com.disaster.dao;

import com.disaster.database.DBConnection;
import com.disaster.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    // ==========================================
    // AUTHENTICATE USER
    // ==========================================

    public User authenticate(
            String username,
            String password
    ) {

        String sql =
                "SELECT user_id, name, username, password, role " +
                        "FROM users " +
                        "WHERE username = ? AND password = ?";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, username);
            statement.setString(2, password);

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {

                    User user = new User();

                    user.setUserId(
                            resultSet.getInt("user_id")
                    );

                    user.setName(
                            resultSet.getString("name")
                    );

                    user.setUsername(
                            resultSet.getString("username")
                    );

                    user.setPassword(
                            resultSet.getString("password")
                    );

                    user.setRole(
                            resultSet.getString("role")
                    );

                    return user;
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Error authenticating user."
            );

            e.printStackTrace();
        }

        return null;
    }
}
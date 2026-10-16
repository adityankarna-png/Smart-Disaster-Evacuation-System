package com.disaster.earthquake;

import com.disaster.gui.MainFrame;
import com.disaster.model.User;
import com.disaster.service.SystemMonitor;

import javax.swing.*;

public class EarthquakeGuiTest {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            // ==========================================
            // CREATE TEST USER
            // ==========================================

            User testUser =
                    new User();

            testUser.setName(
                    "System Administrator"
            );

            testUser.setUsername(
                    "admin"
            );

            testUser.setRole(
                    "ADMIN"
            );


            // ==========================================
            // CREATE MAIN FRAME
            // ==========================================

            MainFrame frame =
                    new MainFrame(
                            testUser
                    );


            frame.setVisible(true);


            // ==========================================
            // CREATE TEST EARTHQUAKE
            // ==========================================

            EarthquakeData earthquake =
                    new EarthquakeData(

                            "GUI-TEST-001",

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
            // SHOW TEST ALERT
            // ==========================================

            try {

                java.lang.reflect.Method method =
                        MainFrame.class.getDeclaredMethod(
                                "showEarthquakeAlert",
                                EarthquakeData.class
                        );


                method.setAccessible(true);


                method.invoke(
                        frame,
                        earthquake
                );


            } catch (Exception e) {

                e.printStackTrace();
            }
        });
    }
}
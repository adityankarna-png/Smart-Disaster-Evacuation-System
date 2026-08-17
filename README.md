🚨 Smart Disaster Evacuation System

A Java-based disaster management and evacuation system designed to support emergency response operations through centralized disaster, victim, shelter, resource, and evacuation management.

The system provides a role-based Java Swing interface connected to a MySQL database using JDBC, along with background multithreading services for disaster monitoring, resource monitoring, evacuation processing, notifications, and automated reporting.

📌 Project Overview

The Smart Disaster Evacuation System is designed to help emergency response teams manage disaster situations efficiently from a centralized control center.

The system allows authorized users to:

Register and manage disasters

Register and manage victims

Manage emergency shelters

Manage emergency resources

Create and monitor evacuation records

View real-time operational statistics

Generate emergency reports

Monitor system conditions in the background

Control access based on user roles

The application follows a layered architecture combining a Java Swing presentation layer, business/service layer, JDBC data access layer, MySQL database layer, and a multithreading layer.

🎯 Objectives

Provide a centralized disaster management system

Improve organization of emergency response operations

Manage victims and their evacuation requirements

Track shelter capacity and occupancy

Monitor emergency resources

Support evacuation planning and processing

Provide role-based access to system modules

Generate operational reports and statistics

Perform background monitoring using multithreading

Maintain reliable communication with the MySQL database

🏗️ System Architecture

The system follows a layered architecture:

PRESENTATION LAYER — Java Swing GUI
        ↓
APPLICATION / BUSINESS LAYER
        ↓
MULTITHREADING LAYER
        ↓
DATA ACCESS LAYER — JDBC
        ↓
DATABASE LAYER — MySQL

Multithreading Layer

                    SystemMonitor
                         │
          ┌──────────────┼──────────────┐
          ↓              ↓              ↓
     Disaster        Resource       Evacuation
     Monitoring      Monitoring      Processing
          │              │              │
          └──────────────┼──────────────┘
                         ↓
                 Notification Service
                         │
                         ↓
                  Reporting Service

✨ Features

🔐 Authentication & Role-Based Access

The system provides login authentication with different access permissions.

Supported Roles

Role

Access

ADMIN

Full system access

RESCUE_TEAM

Disaster, Victim, Shelter, Resource & Evacuation operations

MEDICAL_TEAM

Victim and Evacuation operations

Unauthorized modules are automatically hidden from the navigation panel based on the logged-in user's role.

📊 Dashboard

The dashboard provides an overview of the current emergency response situation.

It displays:

Total disasters

Registered victims

Available shelters

Available resources

Database connection status

Evacuation service status

Resource monitoring status

🚨 Disaster Management

The Disaster Management module allows authorized users to:

Register disasters

View registered disasters

Track disaster type

Track disaster location

Monitor disaster status

Update disaster information

Example disaster statuses:

ACTIVE
RESOLVED

👥 Victim Management

The Victim Management module provides functionality for registering and managing affected individuals.

Victim information includes:

Victim ID

Name

Age

Medical condition

Vulnerability score

Evacuation-related information

The system supports victim prioritization for emergency evacuation.

🏠 Shelter Management

The Shelter Management module manages emergency shelters.

It tracks:

Shelter name

Location

Capacity

Available capacity

Current occupancy

Shelter status

📦 Resource Management

Emergency resources can be registered and monitored through the Resource Management module.

The system tracks:

Resource type

Quantity

Location

Availability status

The background Resource Monitoring Thread detects changes in resource availability.

🚑 Evacuation Management

The Evacuation module manages evacuation assignments and processing.

The system maintains information such as:

Evacuation ID

Victim

Shelter

Assigned resource

Evacuation status

Priority

Pending evacuations are monitored by the background Evacuation Processing Thread.

📈 Reports & Analytics

The Reports & Analytics module provides a centralized overview of emergency response statistics.

Disaster Overview

Total disasters

Active disasters

Resolved disasters

Victim Overview

Total victims

High-priority victims

Waiting victims

Shelter Overview

Total shelters

Available shelters

Full shelters

Resource Overview

Resource types

Available resources

Out-of-stock resources

Evacuation Overview

Total evacuations

Pending evacuations

Completed evacuations

Capacity Statistics

Total shelter capacity

Current shelter occupancy

Total resource quantity

🧵 Multithreading

The system implements a dedicated multithreading layer for background operations.

1. Disaster Monitoring Thread

Periodically monitors active disasters and detects changes in the number of active incidents.

2. Resource Monitoring Thread

Monitors resource quantities and out-of-stock resources.

3. Evacuation Processing Thread

Monitors pending evacuation records and detects changes in evacuation processing.

4. Notification Service Thread

Monitors important system conditions and generates notifications for events such as:

New active disasters

Resources becoming unavailable

New pending evacuations

Evacuation updates

5. Reporting Service Thread

Periodically generates automated system reports using the existing ReportDAO.

SystemMonitor

SystemMonitor manages the lifecycle of all background services and provides graceful shutdown.

🗄️ Database

The application uses MySQL as its database.

Main tables:

users
disasters
victims
shelters
resources
evacuations

🛠️ Technologies Used

Technology

Purpose

Java

Core application development

Java Swing

Graphical User Interface

JDBC

Database connectivity

MySQL

Database management

Maven

Project and dependency management

IntelliJ IDEA

Development environment

Git

Version control

GitHub

Source code hosting

Java Multithreading

Background monitoring and processing

📁 Project Structure

SmartDisasterEvacuationSystem/
│
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── disaster/
│                   ├── dao/
│                   │   ├── DisasterDAO.java
│                   │   ├── EvacuationDAO.java
│                   │   ├── ResourceDAO.java
│                   │   ├── ShelterDAO.java
│                   │   ├── VictimDAO.java
│                   │   └── ReportDAO.java
│                   │
│                   ├── database/
│                   │   ├── DBConnection.java
│                   │   └── Main.java
│                   │
│                   ├── exceptions/
│                   │   ├── NoResourceAvailableException.java
│                   │   ├── NoShelterAvailableException.java
│                   │   └── VictimNotFoundException.java
│                   │
│                   ├── gui/
│                   │   ├── LoginFrame.java
│                   │   ├── MainFrame.java
│                   │   ├── DashboardPanel.java
│                   │   ├── DisasterPanel.java
│                   │   ├── VictimPanel.java
│                   │   ├── ShelterPanel.java
│                   │   ├── ResourcePanel.java
│                   │   ├── EvacuationPanel.java
│                   │   └── ReportPanel.java
│                   │
│                   ├── model/
│                   │   ├── Disaster.java
│                   │   ├── Evacuation.java
│                   │   ├── Resource.java
│                   │   ├── Shelter.java
│                   │   └── Victim.java
│                   │
│                   └── service/
│                       ├── EvacuationService.java
│                       ├── EvacuationServiceImpl.java
│                       ├── DisasterMonitoringThread.java
│                       ├── ResourceMonitoringThread.java
│                       ├── EvacuationProcessingThread.java
│                       ├── NotificationServiceThread.java
│                       ├── ReportingServiceThread.java
│                       └── SystemMonitor.java
│
├── pom.xml
├── .gitignore
└── README.md

⚙️ Requirements

Before running the project, make sure you have:

Java JDK 21 or later

MySQL Server

MySQL Workbench (recommended)

Maven

IntelliJ IDEA or another Java IDE

🚀 Setup & Installation

1. Clone the repository

git clone https://github.com/adityankarna-png/Smart-Disaster-Evacuation-System.git
cd Smart-Disaster-Evacuation-System

2. Configure MySQL

Create the database:

CREATE DATABASE disaster_management;

Then create the required tables:

users
disasters
victims
shelters
resources
evacuations

3. Configure Database Connection

Open:

src/main/java/com/disaster/database/DBConnection.java

Configure your local MySQL credentials.

Example:

private static final String URL =
        "jdbc:mysql://localhost:3306/disaster_management";

private static final String USER =
        "YOUR_USER";

private static final String PASSWORD =
        "YOUR_PASSWORD";

Important: Do not commit your real database password to GitHub.

4. Build the project

mvn clean compile

5. Run the application

Run the application's login frame from your IDE.

Application flow:

Login
  ↓
Authentication
  ↓
Main Dashboard
  ↓
SystemMonitor
  ↓
Background Services

👤 Demo Accounts

The project includes demo accounts for testing role-based access.

Role

Username

Password

Administrator

admin

admin123

Rescue Team

rescue

rescue123

Medical Team

medical

medical123

These credentials are intended for local/demo testing only.

🧪 Testing

The project has been tested for:

Database connectivity

User authentication

Role-based permissions

Dashboard statistics

Disaster management

Victim management

Shelter management

Resource management

Evacuation management

Reports & Analytics

Background monitoring

Multithreaded services

Graceful application shutdown

The application successfully starts all five background services and shuts them down cleanly when the application closes.

Example shutdown:

[Disaster Monitoring] Thread exited.
[Reporting Service] Thread exited.
[Resource Monitoring] Thread exited.
[Evacuation Processing] Thread exited.
[Notification Service] Thread exited.

Process finished with exit code 0

🔒 Role Permissions

ADMIN
│
├── Dashboard
├── Disasters
├── Victims
├── Shelters
├── Resources
├── Evacuation
└── Reports

RESCUE_TEAM
│
├── Dashboard
├── Disasters
├── Victims
├── Shelters
├── Resources
└── Evacuation

MEDICAL_TEAM
│
├── Dashboard
├── Victims
└── Evacuation

The interface dynamically hides modules that the logged-in role is not authorized to use.

📊 System Workflow

Disaster Occurs
       ↓
Disaster Registered
       ↓
Affected Areas Identified
       ↓
Victim Data Collected
       ↓
Priority Determined
       ↓
Resources Allocated
       ↓
Evacuation Plan Generated
       ↓
Evacuation Processed
       ↓
Shelter Occupancy Updated
       ↓
Real-Time Monitoring
       ↓
Reports & Analytics

🔮 Future Enhancements

Possible future improvements include:

Interactive maps and live location tracking

SMS/email emergency notifications

Weather API integration

Advanced evacuation route optimization

AI-based disaster risk prediction

Mobile application

Real-time graphical analytics

Cloud database deployment

Multi-location disaster coordination

👨‍💻 Project

Smart Disaster Evacuation & Resource Allocation System

Built using:

Java + Swing + JDBC + MySQL + Maven + Multithreading

⭐ Repository

If you find this project useful, consider giving the repository a ⭐ on GitHub.

Smart Disaster Evacuation System
Emergency Response • Victim Management • Resource Allocation • Evacuation • Analytics

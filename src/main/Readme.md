Smart Disaster Evacuation & Resource Allocation System

A Java desktop application designed to support disaster-response
coordination by managing disasters, victims, shelters, resources, and
evacuation requests in one place. The project uses a Swing graphical
user interface, JDBC for database access, and MySQL for persistent
storage.

> **Project status:** Developed as an academic APP (21CSC203P) project.
> Features and test observations below reflect the development setup
> described by the team. Configure and test the application in your own
> environment before deployment.

Table of Contents

• Overview
• Objectives
• Features
• Technology Stack
• Architecture
• Core Modules
• Evacuation Priority and
Allocation
• Database
• Prerequisites
• Setup and Run
• Testing and Verified Development
Observations
• Screenshots
• Project Structure
• Limitations and Future
Enhancements
• Team
• Academic Information

Overview

During a disaster, response teams need a clear view of affected people,
available shelters, resource stock, and pending evacuations. This
project provides a centralized desktop interface for recording and
monitoring those operational details.

The application separates the Swing user interface from service logic
and database operations. Data is stored in MySQL and accessed through
JDBC and Data Access Object (DAO) classes. Background monitoring
services support periodic checks and notifications.

Important: This is an academic decision-support prototype. It does
not replace official emergency services, verified real-time emergency
feeds, or human judgement during a real disaster.

Objectives

• Maintain records for disasters, victims, shelters, resources, and
evacuations.
• Calculate a priority score for victims to support evacuation
decisions.
• Allocate an available shelter and resource when creating an
evacuation.
• Track evacuation status and shelter/resource availability.
• Display summary statistics through a reporting dashboard.
• Use background threads for periodic monitoring and
notification-related tasks.
• Demonstrate Java OOP, Swing, JDBC/MySQL, exception handling, and
multithreading.

Features

Disaster management

• Maintain disaster records and status information.
• Includes a test-earthquake workflow for exercising the alert and
victim-generation path in the development environment.

Victim management

• Store victim details such as name, age, medical condition,
vulnerability score, zone risk, waiting time, priority score, and
status.
• Support adding, viewing, updating, and deleting victim records
through the relevant application modules.
• Generate sample victims through the test workflow.

Shelter management

• Track shelter capacity and current occupancy.
• Identify shelters that have remaining capacity.
• Update occupancy when an evacuation is created.

Resource management

• Track resource quantities and availability.
• Decrease the selected resource quantity when an evacuation is
created.
• Represent unavailable or exhausted stock in the application.

Evacuation management

• Create evacuation records with a priority score, selected shelter,
selected resource, timestamp, and status.
• Use PENDING as the initial status in the implemented creation
flow.
• Raise application-specific exceptions when the victim, an available
shelter, or an available resource cannot be found.

Dashboard and reports

The reporting panel summarizes information including: - Total, active,
and resolved disasters. - Total, high-priority, and waiting victims. -
Total, available, and full shelters. - Resource types, available
resources, and out-of-stock resources. - Total, pending, and completed
evacuations. - Total shelter capacity, current occupancy, and total
resource quantity.

Report values are loaded from the database. The report panel uses
SwingWorker for background loading so database work does not need to
block the Swing event-dispatch thread.

Monitoring and notifications

• Background services monitor system information, including pending
evacuations and notification conditions.
• The evacuation-processing monitor checks the number of pending
evacuations periodically (every 10 seconds in the current
implementation).
• The test-earthquake path uses simulated earthquake details. It
should not be described as a verified live earthquake feed unless a
real external data source has been configured and tested.

Role-based access

The GUI includes role-oriented permission handling for: - ADMIN -
RESCUE_TEAM - MEDICAL_TEAM

Review the current permission implementation and database/user setup
before relying on these roles for security-sensitive use.

Technology Stack

────────

Technology                          Purpose

────────

Java                                Application logic and
object-oriented design

Java Swing                          Desktop graphical user interface

JDBC                                Java-to-MySQL database connectivity

MySQL                               Persistent application data

DAO classes                         Database access and SQL operations

Service layer                       Application rules and evacuation
processing

Multithreading                      Periodic monitoring and
notification tasks

Custom exceptions                   Handling unavailable victims, shelters, and resources

Exact JDK, MySQL, and IDE versions should be recorded from the
environment used for the final build.

Architecture

The application follows a layered design:

+-------------------------------+
|       Java Swing GUI          |
| Dashboard and Management UI   |
+---------------+---------------+
|
v
+-------------------------------+
|        Service Layer          |
| Priority / Evacuation Rules   |
| Validation and Coordination   |
+---------------+---------------+
|
v
+-------------------------------+
|       DAO Layer + JDBC        |
| SQL queries and transactions  |
+---------------+---------------+
|
v
+-------------------------------+
|          MySQL DB             |
| Disasters, Victims, Shelters, |
| Resources, Evacuations        |
+-------------------------------+

Background monitoring / notification threads
run alongside the GUI and service components.

Typical information flow:

1. A user performs an operation in the Swing interface.
2. The relevant service or controller logic validates and processes the
   request.
3. DAO classes execute database operations through JDBC.
4. MySQL stores or returns the data.
5. The GUI refreshes the relevant table, dashboard, or report.

Core Modules

The project contains model, DAO, service, GUI, exception, and monitoring
components. Names below reflect components used during development; the
exact package layout may vary by local project checkout.

────────

Component                           Responsibility

────────

Victim and related model classes  Represent application data

VictimDAO                         Create, retrieve, update, and
delete victim records

EvacuationDAO, ShelterDAO,      Database access for the respective
ResourceDAO, DisasterDAO        entities

EvacuationService /               Apply priority and
EvacuationServiceImpl             evacuation-creation rules

ReportDAO                         Aggregate database values for the
reporting panel

SystemMonitor                     Coordinate monitoring services

EvacuationProcessingThread        Periodically check pending
evacuation count

NotificationServiceThread         Check system conditions and
notification triggers

WeatherMonitor                    Weather-monitoring component in the
project

MainFrame and Swing panels        Display and manage application data

Evacuation Priority and Allocation

Priority calculation

The implemented priority calculation is based on:

priority score =
vulnerability score
+ zone risk
+ waiting time
+ medical-condition bonus

A bonus of 5 is added when the medical condition is not "None".
Confirm the exact handling of empty or differently capitalized
medical-condition values in the current source code before changing or
documenting the rule as a production policy.

Evacuation creation flow

The current service flow is:

1. Find the victim by ID.
2. Calculate the victim’s priority score.
3. Find a shelter whose occupancy is below capacity.
4. Find a resource with quantity greater than zero.
5. Create an evacuation record with status PENDING and the current
   timestamp.
6. Increase the chosen shelter’s occupancy.
7. Decrease the chosen resource quantity.
8. Update shelter/resource status when the relevant capacity or
   quantity limit is reached.

Application-specific exceptions are used when the victim, an available
shelter, or an available resource is missing.

Allocation note: The current implementation selects an available
shelter and resource according to its query/selection logic. Do not
describe this as a globally optimal routing, shortest-path, or
mathematically optimal resource-allocation algorithm unless those
capabilities are separately implemented and tested.

Database

The application uses MySQL through JDBC. Database access is centralized
through a connection utility such as DBConnection, while individual
DAO classes execute SQL operations.

The database needs to contain the tables and columns expected by the
project’s DAO queries. The exact schema should be taken from the SQL
files or table-creation statements in the project repository; this
README does not invent a schema that may differ from the implementation.

Configure the connection

1. Create a local MySQL database for the project.
2. Create the tables required by the DAO classes using the project’s
   existing SQL/schema setup.
3. Open DBConnection.java.
4. Set the JDBC URL, database username, and password for your local
   environment.
5. Confirm that the MySQL JDBC driver is available to the project.
6. Run the application and verify that the connection succeeds.

Typical JDBC URL format (replace the example values with your actual
configuration):

jdbc:mysql://localhost:3306/your_database_name

Security: Do not commit real database passwords, API keys, or other
secrets. Prefer environment variables or a local configuration file
excluded from version control. If credentials have already been
committed, rotate them.

Prerequisites

• A compatible JDK installed.
• MySQL Server installed and running.
• MySQL Connector/J available on the project classpath or managed by
the build tool.
• The project source code and any existing schema/setup scripts.
• An IDE such as IntelliJ IDEA, Eclipse, or NetBeans, or the build
setup used by the project.

The repository’s actual build system and dependency versions should be
documented once confirmed. No Maven or Gradle command is specified here
because the current build configuration has not been confirmed.

Setup and Run

1. Get the source code. Clone the project repository or open the
   project folder supplied by the team.
2. Prepare MySQL. Start the MySQL service and create the project
   database.
3. Create the schema. Use the SQL/schema instructions already
   included in the source project.
4. Configure JDBC. Update the local values in DBConnection.java
   or the project’s configured database settings.
5. Add dependencies. Ensure the MySQL Connector/J driver is
   included in the IDE/build classpath.
6. Build the project. Build using the project’s actual IDE or
   build-tool configuration and resolve any local dependency errors.
7. Run the entry point. Run the class that launches the Swing
   application (check the project for the main class; MainFrame is
   the main GUI frame, but it may be launched by a separate entry-point
   class).
8. Verify database access. Open the dashboard and confirm that
   records load without SQL/connection errors.
9. Test with non-production data. Use the project’s
   test-earthquake/sample-victim workflow only in a development
   database.

Do not run the sample-data or test-disaster workflow against a
production database unless you intentionally want those records created.

Testing and Verified Development Observations

The following are development observations recorded while working on the
project. They are not a substitute for a reproducible automated test
report.

────────

Test / observation                  Recorded result

────────

Test earthquake workflow            A simulated test-earthquake alert
was configured in the application
workflow.

Sample victim generation            A run for disaster ID 9 generated
five victims, IDs 33–37.

Priority values                     The recorded generated-victim
scores included 23, 31, 19,
15, and 14.

Evacuation creation                 A successful development run
created evacuation ID 16 for
victim ID 34, with priority 31,
shelter Chennai Relief Shelter,
resource Water Bottles, and
status PENDING.

Foreign-key validation              A test using nonexistent disaster
ID 1 failed due to a foreign-key
constraint; subsequent testing used
existing disaster ID 9.

Reporting panel                     The reporting panel was observed working in the development GUI.

These values are examples from a particular development database and are
not guaranteed to match a fresh installation. IDs and available
inventory depend on the local data.

Recommended test checklist

☐ Application connects to MySQL using the configured credentials.
☐ Disaster, victim, shelter, and resource records can be created
and retrieved.
☐ Victim update and deletion work as expected.
☐ Priority score matches the implemented formula.
☐ Evacuation creation fails cleanly for an unknown victim.
☐ Evacuation creation handles no available shelter.
☐ Evacuation creation handles no available resource.
☐ Successful evacuation updates the evacuation record, shelter
occupancy, and resource quantity consistently.
☐ Dashboard totals agree with the database records.
☐ Monitoring and notification threads start and stop cleanly.
☐ GUI remains responsive while report data is loading.
☐ Role-based permissions are verified for each supported role.

Mark a test as passed only after rerunning it against the final
submitted build and recording the evidence.

Screenshots

Add screenshots from the final working build here. Suggested captures:

1. Dashboard overview.
2. Disaster management panel.
3. Victim management panel showing priority fields.
4. Shelter management panel showing capacity and occupancy.
5. Resource management panel showing quantities/status.
6. Evacuation panel showing a pending evacuation.
7. Reports panel.
8. Test-earthquake alert or sample-victim generation flow.

Example Markdown image syntax (replace the paths with committed image
files):

![Dashboard](docs/screenshots/dashboard.png)

Do not add screenshots that expose real personal information, passwords,
or other sensitive data.

Limitations and Future Enhancements

Potential next steps, subject to project scope and implementation:

• Add a documented database schema and repeatable setup script.
• Move credentials to environment-based configuration.
• Add automated unit and integration tests.
• Add transaction handling across evacuation creation, shelter
occupancy, and resource quantity updates so related changes remain
consistent if one operation fails.
• Add audit logs and clearer error messages for failed operations.
• Integrate and validate an official real-time earthquake/weather API
if live monitoring is required.
• Add map-based evacuation routes and verified travel-time
calculations if routing is in scope.
• Improve concurrency control for simultaneous evacuation requests.
• Add role authentication and server-side authorization checks if the
system is expanded beyond a local academic desktop application.
• Add backup, recovery, and deployment instructions.

These are possible enhancements, not claims that the features are
already implemented.

Team

Team member            Registration number

────────

Aditya Nishant Karna   RA2511003050530
Yugandhar Yadav        RA2511003050328
Rahul Yadav            RA2511003050532
Dharmik Sai            RA2511003050347

Academic Information

• Course: APP (21CSC203P)
• Project: Smart Disaster Evacuation & Resource Allocation System
• Batch: Batch 15
• Project Guide: Dr. S. Shargunam

────────

Before submission

☐ Confirm the final source code and repository link.
☐ Record exact JDK, MySQL, and IDE/build-tool versions.
☐ Add the actual schema/setup SQL and verify the instructions from
a clean setup.
☐ Add screenshots from the final build.
☐ Rerun the tests and attach evidence for each claimed result.
☐ Review database transaction consistency and exception handling.
☐ Remove local credentials and private data from the repository.
☐ Replace any project-specific setup assumptions with verified
instructions.
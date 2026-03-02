# E-Drop: E-Waste Collection Management System

## Overview

E-Drop is a Java-based desktop application developed to promote
responsible electronic waste disposal and management. The system
provides a structured platform where customers can request e-waste
pickups, collectors can manage and complete those requests, and
administrators can oversee the entire process.

The application is built using Java Swing for the frontend and Oracle
Database for backend data management.

------------------------------------------------------------------------

## Objective

The goal of this system is to:

-   Encourage proper disposal of electronic waste
-   Provide an organized pickup scheduling system
-   Enable role-based access for Customers, Collectors, and Admin
-   Allow centralized monitoring and management of users and requests

This project aligns with Sustainable Development Goal (SDG) 12 --
Responsible Consumption and Production.

------------------------------------------------------------------------

## Technology Stack

Frontend: - Java Swing - Java AWT

Backend: - Oracle Database 23c Free - JDBC (ojdbc11)

IDE: - NetBeans 22

Language: - Java (JDK 23)

------------------------------------------------------------------------

## System Architecture

### Admin

-   View total users, customers, and collectors
-   Activate / Deactivate users
-   Monitor system activity
-   View all registered data

### Customer

-   Register and login
-   Add e-waste items
-   Schedule pickup requests
-   Track request status

### Collector

-   Register and login
-   View assigned pickup requests
-   Update pickup status

------------------------------------------------------------------------

## Database Tables

-   USERS
-   CUSTOMERS
-   COLLECTORS
-   CUST_EWASTE_ITEMS
-   PICKUP

Key Features: - Foreign key relationships - Role-based login
validation - Status-based soft deletion (active/inactive) -
Sequence-based primary key generation

------------------------------------------------------------------------

## Key Features

-   Single Login System for Admin, Customer, and Collector
-   Role-Based Access Control
-   Soft Deactivation (Users cannot login if inactive)
-   Dynamic Admin Dashboard
-   Real-time User Count Display
-   Activate / Deactivate Toggle System
-   Form Validations (Username, Password, Email, Age, Phone)

------------------------------------------------------------------------

## Login Credentials

Admin (Hardcoded):

Username: admin\
Password: admin123@

Customer and Collector accounts are stored in the USERS table.

------------------------------------------------------------------------

## How to Run

1.  Install Oracle Database 23c Free.
2.  Create required tables and sequences in FREEPDB1.
3.  Update DBConnection.java with database credentials.
4.  Add ojdbc11.jar to project libraries.
5.  Clean and Build the project.
6.  Run LandingPage.java.

------------------------------------------------------------------------

## Security Design

-   Role validation during login
-   Status-based access control
-   Input validation on all forms
-   Separate handling of Admin authentication
-   Soft delete using status column

------------------------------------------------------------------------

## Future Enhancements

-   Password hashing
-   Email verification
-   Pickup scheduling calendar
-   Report generation
-   Dashboard analytics
-   Search and filtering
-   Notification system

------------------------------------------------------------------------

## Author

Treesa Jose\
Master of Computer Applications

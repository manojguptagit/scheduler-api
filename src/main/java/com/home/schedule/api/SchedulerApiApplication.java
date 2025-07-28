package com.home.schedule.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application class for the Job Scheduler API.
 * 
 * This application provides a comprehensive job scheduling and management system
 * with the following features:
 * - Job definition and management
 * - Cron-based scheduling
 * - Job execution tracking and monitoring
 * - Dependency management between jobs
 * - Notification system
 * - Statistics and analytics
 * - Audit logging
 * - User management
 * 
 * The application uses:
 * - Spring Boot 3.5.4 for the framework
 * - H2 in-memory database for development
 * - Spring JDBC for data access
 * - RESTful API endpoints
 * - Layered architecture (Controller -> Service -> DAO -> Database)
 * 
 * @author Job Scheduler Team
 * @version 1.0.0
 * @since 2024
 */
@SpringBootApplication
public class SchedulerApiApplication {

    /**
     * Main method that bootstraps the Spring Boot application.
     * 
     * This method:
     * 1. Initializes the Spring application context
     * 2. Starts the embedded web server (Tomcat by default)
     * 3. Loads the H2 in-memory database with schema and sample data
     * 4. Registers all Spring beans and components
     * 5. Makes the application ready to accept HTTP requests
     * 
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(SchedulerApiApplication.class, args);
    }

}

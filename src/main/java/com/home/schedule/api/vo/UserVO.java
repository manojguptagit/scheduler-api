package com.home.schedule.api.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Value Object (VO) representing a user in the job scheduling system.
 * 
 * This class encapsulates user information for authentication, authorization,
 * and audit purposes. It maps to the 'users' table in the database and is used
 * throughout the application for user management operations.
 * 
 * Features:
 * - User authentication and identification
 * - User profile information (name, email)
 * - Account status management (active/inactive)
 * - Audit trail support (creation and update timestamps)
 * - Secure password handling (stored as hash)
 * 
 * Lombok annotations provide:
 * - @Data: Getters, setters, toString, equals, hashCode
 * - @NoArgsConstructor: Default constructor
 * - @AllArgsConstructor: Constructor with all fields
 * - @Builder: Builder pattern for object creation
 * 
 * @author Job Scheduler Team
 * @version 1.0.0
 * @since 2024
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVO {
    
    /**
     * Unique identifier for the user (Primary Key)
     */
    private Long id;
    
    /**
     * Unique username for login and identification
     * Must be unique across all users
     */
    private String username;
    
    /**
     * User's email address for notifications and contact
     * Must be unique across all users
     */
    private String email;
    
    /**
     * Hashed password for secure authentication
     * Should never be stored in plain text
     */
    private String passwordHash;
    
    /**
     * User's first name for display and identification
     */
    private String firstName;
    
    /**
     * User's last name for display and identification
     */
    private String lastName;
    
    /**
     * Flag indicating whether the user account is active
     * Inactive users cannot log in or perform operations
     */
    private Boolean isActive;
    
    /**
     * Timestamp when the user account was created
     * Automatically set by the database
     */
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the user account was last updated
     * Automatically updated by the database on each modification
     */
    private LocalDateTime updatedAt;
} 
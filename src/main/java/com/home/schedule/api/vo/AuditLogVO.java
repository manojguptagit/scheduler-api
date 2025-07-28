package com.home.schedule.api.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Value Object (VO) representing audit log entries for system activities.
 * 
 * This class encapsulates comprehensive audit information for all system activities,
 * providing a complete audit trail for compliance, security, and troubleshooting.
 * It maps to the 'audit_log' table in the database and enables detailed activity tracking.
 * 
 * Features:
 * - Complete audit trail for all system activities
 * - User action tracking and accountability
 * - Before/after value comparison for changes
 * - IP address and user agent tracking
 * - Comprehensive activity logging
 * 
 * Audit Actions:
 * - CREATE: New entity creation
 * - UPDATE: Entity modification
 * - DELETE: Entity deletion
 * - EXECUTE: Job execution
 * - LOGIN: User authentication
 * - LOGOUT: User logout
 * - SCHEDULE: Job scheduling
 * - CANCEL: Job cancellation
 * - CONFIGURE: System configuration changes
 * 
 * Entity Types:
 * - USER: User management activities
 * - JOB: Job definition and management
 * - SCHEDULE: Schedule configuration
 * - EXECUTION: Job execution activities
 * - NOTIFICATION: Notification activities
 * - CONFIG: System configuration
 * 
 * Use Cases:
 * - Compliance and regulatory requirements
 * - Security monitoring and incident investigation
 * - User activity tracking and accountability
 * - Change management and version control
 * - Troubleshooting and debugging
 * - Performance analysis and optimization
 * 
 * Security Features:
 * - Complete audit trail for all activities
 * - IP address tracking for security monitoring
 * - User agent information for client identification
 * - Before/after value comparison for change tracking
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
public class AuditLogVO {
    
    /**
     * Unique identifier for the audit log entry (Primary Key)
     */
    private Long id;
    
    /**
     * ID of the user who performed the action (Foreign Key to users table)
     * May be null for system-generated actions
     */
    private Long userId;
    
    /**
     * Type of action performed
     * Values: CREATE, UPDATE, DELETE, EXECUTE, LOGIN, LOGOUT, SCHEDULE, CANCEL, CONFIGURE
     */
    private String action;
    
    /**
     * Type of entity that was affected
     * Values: USER, JOB, SCHEDULE, EXECUTION, NOTIFICATION, CONFIG
     */
    private String entityType;
    
    /**
     * ID of the entity that was affected
     * References the specific entity that was created, updated, or deleted
     */
    private Long entityId;
    
    /**
     * Previous values before the change (JSON format)
     * Contains the state of the entity before modification
     * Null for CREATE actions
     */
    private String oldValues;
    
    /**
     * New values after the change (JSON format)
     * Contains the state of the entity after modification
     * Null for DELETE actions
     */
    private String newValues;
    
    /**
     * IP address of the client that performed the action
     * Used for security monitoring and geolocation tracking
     */
    private String ipAddress;
    
    /**
     * User agent string from the client
     * Contains browser/client information for debugging
     */
    private String userAgent;
    
    /**
     * Timestamp when the audit log entry was created
     * Automatically set by the database
     */
    private LocalDateTime createdAt;
    
    // Additional fields for convenience - not stored in database
    
    /**
     * The user who performed the action
     * Populated when retrieving audit log with user details
     */
    private UserVO user;
} 
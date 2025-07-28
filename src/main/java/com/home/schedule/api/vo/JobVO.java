package com.home.schedule.api.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Value Object (VO) representing a job definition in the scheduling system.
 * 
 * This class encapsulates job information including its definition, configuration,
 * and relationships with other entities. It maps to the 'jobs' table in the database
 * and serves as the central entity for job management operations.
 * 
 * Features:
 * - Job definition and configuration
 * - Parameter management for job execution
 * - Status management (active/inactive)
 * - Relationship management (schedules, dependencies, notifications)
 * - Audit trail support (creation and update timestamps)
 * - Statistics tracking
 * 
 * Relationships:
 * - Can have multiple schedules (many-to-many via job_schedules table)
 * - Can have dependencies on other jobs
 * - Can have notification configurations
 * - Tracks execution statistics
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
public class JobVO {
    
    /**
     * Unique identifier for the job (Primary Key)
     */
    private Long id;
    
    /**
     * Human-readable name for the job
     * Used for display and identification purposes
     */
    private String name;
    
    /**
     * Detailed description of what the job does
     * Provides context for job purpose and functionality
     */
    private String description;
    
    /**
     * Fully qualified class name of the job implementation
     * The class must implement the job execution logic
     */
    private String jobClass;
    
    /**
     * JSON string containing job parameters
     * Configurable parameters passed to the job during execution
     */
    private String parameters;
    
    /**
     * Flag indicating whether the job is active and can be executed
     * Inactive jobs are not scheduled or executed
     */
    private Boolean isActive;
    
    /**
     * ID of the user who created this job (Foreign Key to users table)
     * Used for audit and authorization purposes
     */
    private Long createdBy;
    
    /**
     * Timestamp when the job was created
     * Automatically set by the database
     */
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the job was last updated
     * Automatically updated by the database on each modification
     */
    private LocalDateTime updatedAt;
    
    // Additional fields for convenience - not stored in database
    
    /**
     * List of schedules associated with this job
     * Populated when retrieving job with its schedules
     */
    private List<ScheduleVO> schedules;
    
    /**
     * List of dependencies for this job
     * Jobs that must complete before this job can execute
     */
    private List<JobDependencyVO> dependencies;
    
    /**
     * List of notification configurations for this job
     * Defines when and how to notify about job execution
     */
    private List<JobNotificationVO> notifications;
    
    /**
     * Statistics for this job
     * Contains execution metrics and performance data
     */
    private JobStatisticsVO statistics;
} 
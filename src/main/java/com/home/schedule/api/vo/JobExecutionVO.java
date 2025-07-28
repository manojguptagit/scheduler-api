package com.home.schedule.api.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Value Object (VO) representing a job execution instance in the scheduling system.
 * 
 * This class encapsulates information about a single execution of a job, including
 * its status, timing, results, and any errors that occurred. It maps to the
 * 'job_executions' table in the database and provides comprehensive execution tracking.
 * 
 * Features:
 * - Execution status tracking (PENDING, RUNNING, COMPLETED, FAILED, CANCELLED)
 * - Timing information (start time, end time, duration)
 * - Result and error message storage
 * - Parameter tracking for the specific execution
 * - Relationship with job and schedule
 * - Step-by-step execution tracking
 * - Notification history
 * 
 * Execution Status Values:
 * - PENDING: Job is queued for execution
 * - RUNNING: Job is currently executing
 * - COMPLETED: Job completed successfully
 * - FAILED: Job failed with an error
 * - CANCELLED: Job was cancelled before completion
 * 
 * Relationships:
 * - Belongs to a specific job (jobId)
 * - May be triggered by a specific schedule (scheduleId)
 * - Contains multiple execution steps
 * - Has notification history
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
public class JobExecutionVO {
    
    /**
     * Unique identifier for the job execution (Primary Key)
     */
    private Long id;
    
    /**
     * ID of the job that was executed (Foreign Key to jobs table)
     */
    private Long jobId;
    
    /**
     * ID of the schedule that triggered this execution (Foreign Key to schedules table)
     * May be null if job was executed manually
     */
    private Long scheduleId;
    
    /**
     * Unique execution identifier (UUID format)
     * Used for tracking and correlation across systems
     */
    private String executionId;
    
    /**
     * Current status of the job execution
     * Values: PENDING, RUNNING, COMPLETED, FAILED, CANCELLED
     */
    private String status;
    
    /**
     * Timestamp when the job execution started
     * Set when job begins execution
     */
    private LocalDateTime startTime;
    
    /**
     * Timestamp when the job execution completed
     * Set when job finishes (successfully or with error)
     */
    private LocalDateTime endTime;
    
    /**
     * Duration of the job execution in milliseconds
     * Calculated as endTime - startTime
     */
    private Long durationMs;
    
    /**
     * Success message or result from job execution
     * Populated when job completes successfully
     */
    private String resultMessage;
    
    /**
     * Error message if job execution failed
     * Populated when job fails
     */
    private String errorMessage;
    
    /**
     * Full stack trace if job execution failed
     * Provides detailed error information for debugging
     */
    private String stackTrace;
    
    /**
     * Parameters used for this specific execution
     * JSON string containing execution-specific parameters
     */
    private String parameters;
    
    /**
     * Timestamp when the execution record was created
     * Automatically set by the database
     */
    private LocalDateTime createdAt;
    
    // Additional fields for convenience - not stored in database
    
    /**
     * The job that was executed
     * Populated when retrieving execution with job details
     */
    private JobVO job;
    
    /**
     * The schedule that triggered this execution
     * Populated when retrieving execution with schedule details
     */
    private ScheduleVO schedule;
    
    /**
     * List of execution steps for this job execution
     * Provides detailed step-by-step execution tracking
     */
    private List<JobExecutionStepVO> steps;
    
    /**
     * List of notifications sent for this execution
     * Tracks all notification attempts and results
     */
    private List<NotificationHistoryVO> notifications;
} 
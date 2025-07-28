package com.home.schedule.api.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Value Object (VO) representing a step within a job execution.
 * 
 * This class encapsulates information about individual steps that occur during
 * a job execution, providing detailed tracking of the execution process. It maps
 * to the 'job_execution_steps' table in the database and enables granular
 * monitoring of job execution progress.
 * 
 * Features:
 * - Step-by-step execution tracking
 * - Individual step status monitoring
 * - Timing information for each step
 * - Result and error message storage per step
 * - Ordered execution sequence
 * 
 * Step Status Values:
 * - PENDING: Step is queued for execution
 * - RUNNING: Step is currently executing
 * - COMPLETED: Step completed successfully
 * - FAILED: Step failed with an error
 * - SKIPPED: Step was skipped due to conditions
 * 
 * Use Cases:
 * - Complex job execution with multiple phases
 * - Detailed progress tracking for long-running jobs
 * - Debugging and troubleshooting execution issues
 * - Performance analysis of individual steps
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
public class JobExecutionStepVO {
    
    /**
     * Unique identifier for the execution step (Primary Key)
     */
    private Long id;
    
    /**
     * ID of the job execution this step belongs to (Foreign Key to job_executions table)
     */
    private Long jobExecutionId;
    
    /**
     * Human-readable name for the step
     * Used for identification and display purposes
     */
    private String stepName;
    
    /**
     * Order of execution for this step within the job execution
     * Lower numbers execute first
     */
    private Integer stepOrder;
    
    /**
     * Current status of the step execution
     * Values: PENDING, RUNNING, COMPLETED, FAILED, SKIPPED
     */
    private String status;
    
    /**
     * Timestamp when the step execution started
     * Set when step begins execution
     */
    private LocalDateTime startTime;
    
    /**
     * Timestamp when the step execution completed
     * Set when step finishes (successfully or with error)
     */
    private LocalDateTime endTime;
    
    /**
     * Duration of the step execution in milliseconds
     * Calculated as endTime - startTime
     */
    private Long durationMs;
    
    /**
     * Success message or result from step execution
     * Populated when step completes successfully
     */
    private String resultMessage;
    
    /**
     * Error message if step execution failed
     * Populated when step fails
     */
    private String errorMessage;
    
    /**
     * Timestamp when the step record was created
     * Automatically set by the database
     */
    private LocalDateTime createdAt;
} 
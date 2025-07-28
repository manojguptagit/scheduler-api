package com.home.schedule.api.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Value Object (VO) representing a dependency relationship between jobs.
 * 
 * This class encapsulates information about job dependencies, defining which jobs
 * must complete before other jobs can execute. It maps to the 'job_dependencies'
 * table in the database and enables complex job orchestration workflows.
 * 
 * Features:
 * - Job dependency management
 * - Dependency type classification
 * - Workflow orchestration support
 * - Audit trail for dependency creation
 * 
 * Dependency Types:
 * - REQUIRED: Dependent job must complete successfully before prerequisite job can start
 * - OPTIONAL: Dependent job can start even if prerequisite job fails
 * - CONDITIONAL: Dependent job starts based on prerequisite job's result
 * - SEQUENTIAL: Jobs must execute in specific order
 * 
 * Use Cases:
 * - Data processing pipelines where jobs depend on previous jobs
 * - ETL workflows with multiple stages
 * - Complex business processes with dependencies
 * - Resource management and scheduling optimization
 * 
 * Relationships:
 * - Links a dependent job to its prerequisite job
 * - Enables workflow orchestration and scheduling
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
public class JobDependencyVO {
    
    /**
     * Unique identifier for the dependency (Primary Key)
     */
    private Long id;
    
    /**
     * ID of the job that depends on another job (Foreign Key to jobs table)
     * This job cannot start until the prerequisite job completes
     */
    private Long dependentJobId;
    
    /**
     * ID of the job that must complete first (Foreign Key to jobs table)
     * This job must complete before the dependent job can start
     */
    private Long prerequisiteJobId;
    
    /**
     * Type of dependency relationship
     * Values: REQUIRED, OPTIONAL, CONDITIONAL, SEQUENTIAL
     */
    private String dependencyType;
    
    /**
     * Timestamp when the dependency was created
     * Automatically set by the database
     */
    private LocalDateTime createdAt;
    
    // Additional fields for convenience - not stored in database
    
    /**
     * The job that depends on another job
     * Populated when retrieving dependency with job details
     */
    private JobVO dependentJob;
    
    /**
     * The job that must complete first
     * Populated when retrieving dependency with job details
     */
    private JobVO prerequisiteJob;
} 
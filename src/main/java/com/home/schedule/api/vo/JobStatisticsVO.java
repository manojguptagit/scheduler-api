package com.home.schedule.api.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Value Object (VO) representing statistics for a job on a specific date.
 * 
 * This class encapsulates performance metrics and execution statistics for jobs,
 * providing insights into job behavior and system performance. It maps to the
 * 'job_statistics' table in the database and enables comprehensive analytics.
 * 
 * Features:
 * - Daily execution statistics aggregation
 * - Success and failure rate tracking
 * - Performance metrics (duration, throughput)
 * - Historical trend analysis
 * - Performance monitoring and alerting
 * 
 * Statistics Tracked:
 * - Total number of executions per day
 * - Successful vs failed execution counts
 * - Duration metrics (total, average, min, max)
 * - Success and failure rates (calculated)
 * - Performance trends over time
 * 
 * Use Cases:
 * - Monitor job performance and reliability
 * - Identify performance bottlenecks
 * - Track system health and stability
 * - Generate reports and dashboards
 * - Set up performance alerts and thresholds
 * - Capacity planning and resource allocation
 * 
 * Relationships:
 * - Belongs to a specific job
 * - Aggregates data for a specific date
 * - Enables historical analysis and trending
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
public class JobStatisticsVO {
    
    /**
     * Unique identifier for the statistics record (Primary Key)
     */
    private Long id;
    
    /**
     * ID of the job these statistics belong to (Foreign Key to jobs table)
     */
    private Long jobId;
    
    /**
     * Date for which these statistics are aggregated
     * Statistics are calculated per day for trend analysis
     */
    private LocalDate date;
    
    /**
     * Total number of job executions on this date
     * Includes both successful and failed executions
     */
    private Integer totalExecutions;
    
    /**
     * Number of successful job executions on this date
     * Jobs that completed without errors
     */
    private Integer successfulExecutions;
    
    /**
     * Number of failed job executions on this date
     * Jobs that completed with errors or exceptions
     */
    private Integer failedExecutions;
    
    /**
     * Total duration of all job executions in milliseconds
     * Sum of all execution durations for this date
     */
    private Long totalDurationMs;
    
    /**
     * Average duration of job executions in milliseconds
     * Calculated as totalDurationMs / totalExecutions
     */
    private Long avgDurationMs;
    
    /**
     * Minimum duration of job executions in milliseconds
     * Shortest execution time on this date
     */
    private Long minDurationMs;
    
    /**
     * Maximum duration of job executions in milliseconds
     * Longest execution time on this date
     */
    private Long maxDurationMs;
    
    /**
     * Timestamp when the statistics record was created
     * Automatically set by the database
     */
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the statistics record was last updated
     * Automatically updated by the database on each modification
     */
    private LocalDateTime updatedAt;
    
    // Additional fields for convenience - not stored in database
    
    /**
     * The job these statistics belong to
     * Populated when retrieving statistics with job details
     */
    private JobVO job;
    
    /**
     * Success rate as a percentage (0.0 to 100.0)
     * Calculated as (successfulExecutions / totalExecutions) * 100
     */
    private Double successRate;
    
    /**
     * Failure rate as a percentage (0.0 to 100.0)
     * Calculated as (failedExecutions / totalExecutions) * 100
     */
    private Double failureRate;
} 
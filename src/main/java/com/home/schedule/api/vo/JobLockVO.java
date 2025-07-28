package com.home.schedule.api.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Value Object (VO) representing a lock on a job to prevent concurrent execution.
 * 
 * This class encapsulates information about job locks that prevent multiple
 * instances of the same job from running simultaneously. It maps to the
 * 'job_locks' table in the database and provides distributed locking capabilities.
 * 
 * Features:
 * - Concurrent execution prevention
 * - Distributed locking support
 * - Lock expiration management
 * - Lock holder identification
 * - Automatic lock cleanup
 * 
 * Use Cases:
 * - Prevent multiple instances of the same job from running
 * - Ensure data consistency in distributed environments
 * - Implement job queuing and serialization
 * - Handle long-running jobs that should not overlap
 * - Resource contention management
 * 
 * Lock Behavior:
 * - Only one lock can exist per job at any time
 * - Locks have expiration times to prevent deadlocks
 * - Expired locks are automatically cleaned up
 * - Lock holder information enables debugging and monitoring
 * 
 * Relationships:
 * - Belongs to a specific job (one-to-one relationship)
 * - Prevents concurrent execution of the same job
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
public class JobLockVO {
    
    /**
     * Unique identifier for the job lock (Primary Key)
     */
    private Long id;
    
    /**
     * ID of the job that is locked (Foreign Key to jobs table)
     * Only one lock can exist per job
     */
    private Long jobId;
    
    /**
     * Identifier of the process or thread holding the lock
     * Used for debugging and monitoring purposes
     * Format: "hostname:processId:threadId" or custom identifier
     */
    private String lockHolder;
    
    /**
     * Timestamp when the lock was acquired
     * Used for lock duration tracking
     */
    private LocalDateTime lockTime;
    
    /**
     * Timestamp when the lock expires
     * Prevents deadlocks by automatically expiring locks
     * Expired locks are cleaned up by background processes
     */
    private LocalDateTime expiresAt;
    
    // Additional fields for convenience - not stored in database
    
    /**
     * The job that is locked
     * Populated when retrieving lock with job details
     */
    private JobVO job;
} 
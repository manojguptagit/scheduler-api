package com.home.schedule.api.service;

import com.home.schedule.api.dao.JobDAO;
import com.home.schedule.api.dao.JobExecutionDAO;
import com.home.schedule.api.dao.ScheduleDAO;
import com.home.schedule.api.vo.JobVO;
import com.home.schedule.api.vo.JobExecutionVO;
import com.home.schedule.api.vo.ScheduleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for job management operations.
 * 
 * This class provides comprehensive business logic for job-related operations
 * including CRUD operations, job execution, scheduling, and management.
 * It orchestrates interactions between multiple DAOs and provides
 * transaction management for complex operations.
 * 
 * Features:
 * - Complete job lifecycle management
 * - Job execution and scheduling
 * - Validation and business rules enforcement
 * - Transaction management
 * - Error handling and exception management
 * - Job health monitoring
 * - Bulk operations support
 * 
 * Business Rules:
 * - Job names must be unique
 * - Job classes must be unique
 * - Cannot delete jobs that are currently running
 * - Job execution requires proper validation
 * - Scheduling operations are transactional
 * 
 * Dependencies:
 * - JobDAO: For job data access operations
 * - JobExecutionDAO: For execution tracking
 * - ScheduleDAO: For schedule management
 * - JobSchedulerService: For scheduling operations
 * 
 * @author Job Scheduler Team
 * @version 1.0.0
 * @since 2024
 */
@Service
@Transactional
public class JobService {

    /**
     * Data access object for job operations
     */
    @Autowired
    private JobDAO jobDAO;

    /**
     * Data access object for job execution operations
     */
    @Autowired
    private JobExecutionDAO jobExecutionDAO;

    /**
     * Data access object for schedule operations
     */
    @Autowired
    private ScheduleDAO scheduleDAO;

    /**
     * Service for job scheduling operations
     */
    @Autowired
    private JobSchedulerService jobSchedulerService;

    // Job CRUD Operations

    /**
     * Retrieve all jobs from the system
     * 
     * @return List of all jobs ordered by creation date
     */
    public List<JobVO> getAllJobs() {
        return jobDAO.findAll();
    }

    /**
     * Retrieve all active jobs from the system
     * 
     * @return List of active jobs ordered by creation date
     */
    public List<JobVO> getActiveJobs() {
        return jobDAO.findActiveJobs();
    }

    /**
     * Find a job by its unique ID
     * 
     * @param id The job's unique identifier
     * @return Optional containing the job if found, empty otherwise
     */
    public Optional<JobVO> getJobById(Long id) {
        return jobDAO.findById(id);
    }

    /**
     * Find jobs by name (supports partial matching)
     * 
     * @param name The job name to search for
     * @return List of jobs with matching names
     */
    public List<JobVO> getJobsByName(String name) {
        return jobDAO.findByName(name);
    }

    /**
     * Find jobs by job class
     * 
     * @param jobClass The job class to search for
     * @return List of jobs with the specified job class
     */
    public List<JobVO> getJobsByJobClass(String jobClass) {
        return jobDAO.findByJobClass(jobClass);
    }

    /**
     * Find jobs created by a specific user
     * 
     * @param createdBy The ID of the user who created the jobs
     * @return List of jobs created by the specified user
     */
    public List<JobVO> getJobsByCreator(Long createdBy) {
        return jobDAO.findByCreatedBy(createdBy);
    }

    /**
     * Create a new job with validation and scheduling
     * 
     * @param job The job to create
     * @return The created job with generated ID
     * @throws IllegalArgumentException if job name or class already exists
     */
    public JobVO createJob(JobVO job) {
        // Validate job name uniqueness
        if (jobDAO.existsByName(job.getName())) {
            throw new IllegalArgumentException("Job with name '" + job.getName() + "' already exists");
        }
        
        // Validate job class uniqueness
        if (jobDAO.existsByJobClass(job.getJobClass())) {
            throw new IllegalArgumentException("Job class '" + job.getJobClass() + "' is already registered");
        }

        // Save the job
        JobVO savedJob = jobDAO.save(job);
        
        // Schedule the job if schedules are provided
        if (job.getSchedules() != null && !job.getSchedules().isEmpty()) {
            for (ScheduleVO schedule : job.getSchedules()) {
                jobSchedulerService.scheduleJob(savedJob.getId(), schedule.getId());
            }
        }

        return savedJob;
    }

    /**
     * Update an existing job
     * 
     * @param id The ID of the job to update
     * @param job The updated job data
     * @return The updated job
     * @throws IllegalArgumentException if job with specified ID doesn't exist
     */
    public JobVO updateJob(Long id, JobVO job) {
        Optional<JobVO> existingJob = jobDAO.findById(id);
        if (existingJob.isEmpty()) {
            throw new IllegalArgumentException("Job with id " + id + " not found");
        }

        job.setId(id);
        return jobDAO.save(job);
    }

    /**
     * Delete a job with safety checks
     * 
     * @param id The ID of the job to delete
     * @return true if the job was successfully deleted, false otherwise
     * @throws IllegalStateException if job is currently running
     */
    public boolean deleteJob(Long id) {
        // Check if job is currently running
        List<JobExecutionVO> runningExecutions = jobExecutionDAO.findRunningExecutionsByJobId(id);
        if (!runningExecutions.isEmpty()) {
            throw new IllegalStateException("Cannot delete job that is currently running");
        }

        // Cancel any pending executions
        jobSchedulerService.cancelJobExecutions(id);

        return jobDAO.deleteById(id);
    }

    /**
     * Activate a job (enable it for execution)
     * 
     * @param id The ID of the job to activate
     * @return true if the job was successfully activated, false otherwise
     */
    public boolean activateJob(Long id) {
        boolean activated = jobDAO.activateJob(id);
        if (activated) {
            // Reactivate any associated schedules
            jobSchedulerService.resumeJob(id);
        }
        return activated;
    }

    /**
     * Deactivate a job (disable it from execution)
     * 
     * @param id The ID of the job to deactivate
     * @return true if the job was successfully deactivated, false otherwise
     */
    public boolean deactivateJob(Long id) {
        boolean deactivated = jobDAO.deactivateJob(id);
        if (deactivated) {
            // Pause any associated schedules
            jobSchedulerService.pauseJob(id);
        }
        return deactivated;
    }

    /**
     * Update job parameters
     * 
     * @param id The ID of the job
     * @param parameters The new parameters (JSON string)
     * @return true if the parameters were successfully updated, false otherwise
     */
    public boolean updateJobParameters(Long id, String parameters) {
        return jobDAO.updateParameters(id, parameters);
    }

    // Job Execution Operations

    /**
     * Execute a job with default parameters
     * 
     * @param jobId The ID of the job to execute
     * @return The job execution record
     */
    public JobExecutionVO executeJob(Long jobId) {
        return executeJob(jobId, null);
    }

    /**
     * Execute a job with custom parameters
     * 
     * @param jobId The ID of the job to execute
     * @param parameters Custom parameters for the execution (JSON string)
     * @return The job execution record
     */
    public JobExecutionVO executeJob(Long jobId, String parameters) {
        // Validate job exists and is active
        Optional<JobVO> job = jobDAO.findById(jobId);
        if (job.isEmpty()) {
            throw new IllegalArgumentException("Job with id " + jobId + " not found");
        }
        
        if (!job.get().getIsActive()) {
            throw new IllegalStateException("Cannot execute inactive job");
        }

        // Create execution record
        JobExecutionVO execution = JobExecutionVO.builder()
                .jobId(jobId)
                .executionId(UUID.randomUUID().toString())
                .status("PENDING")
                .parameters(parameters)
                .createdAt(LocalDateTime.now())
                .build();

        // Save execution record
        JobExecutionVO savedExecution = jobExecutionDAO.save(execution);

        // Execute the job asynchronously
        jobSchedulerService.executeJobAsync(savedExecution);

        return savedExecution;
    }

    /**
     * Get all executions for a specific job
     * 
     * @param jobId The ID of the job
     * @return List of job executions
     */
    public List<JobExecutionVO> getJobExecutions(Long jobId) {
        return jobExecutionDAO.findByJobId(jobId);
    }

    /**
     * Get job executions by status
     * 
     * @param jobId The ID of the job
     * @param status The execution status to filter by
     * @return List of job executions with the specified status
     */
    public List<JobExecutionVO> getJobExecutionsByStatus(Long jobId, String status) {
        return jobExecutionDAO.findByJobIdAndStatus(jobId, status);
    }

    /**
     * Get a specific job execution by ID
     * 
     * @param executionId The execution ID
     * @return Optional containing the execution if found, empty otherwise
     */
    public Optional<JobExecutionVO> getJobExecutionById(Long executionId) {
        return jobExecutionDAO.findById(executionId);
    }

    /**
     * Cancel a running job execution
     * 
     * @param executionId The execution ID to cancel
     * @return true if the execution was successfully cancelled, false otherwise
     */
    public boolean cancelJobExecution(Long executionId) {
        return jobSchedulerService.cancelJobExecution(executionId);
    }

    // Job Scheduling Operations

    /**
     * Schedule a job with a specific schedule
     * 
     * @param jobId The ID of the job
     * @param scheduleId The ID of the schedule
     * @return true if the job was successfully scheduled, false otherwise
     */
    public boolean scheduleJob(Long jobId, Long scheduleId) {
        return jobSchedulerService.scheduleJob(jobId, scheduleId);
    }

    /**
     * Remove a job from a specific schedule
     * 
     * @param jobId The ID of the job
     * @param scheduleId The ID of the schedule
     * @return true if the job was successfully unscheduled, false otherwise
     */
    public boolean unscheduleJob(Long jobId, Long scheduleId) {
        return jobSchedulerService.unscheduleJob(jobId, scheduleId);
    }

    /**
     * Get all schedules associated with a job
     * 
     * @param jobId The ID of the job
     * @return List of schedules associated with the job
     */
    public List<ScheduleVO> getJobSchedules(Long jobId) {
        return scheduleDAO.findByJobId(jobId);
    }

    /**
     * Check if a job has any active schedules
     * 
     * @param jobId The ID of the job
     * @return true if the job has active schedules, false otherwise
     */
    public boolean isJobScheduled(Long jobId) {
        return jobSchedulerService.isJobScheduled(jobId);
    }

    // Statistical and Query Operations

    /**
     * Get the total number of jobs in the system
     * 
     * @return Total count of jobs
     */
    public long getTotalJobCount() {
        return jobDAO.count();
    }

    /**
     * Get the number of active jobs in the system
     * 
     * @return Count of active jobs
     */
    public long getActiveJobCount() {
        return jobDAO.countActiveJobs();
    }

    /**
     * Get jobs that have associated schedules
     * 
     * @return List of jobs with schedules
     */
    public List<JobVO> getJobsWithSchedules() {
        return jobDAO.findJobsWithSchedules();
    }

    /**
     * Get jobs that have no associated schedules
     * 
     * @return List of jobs without schedules
     */
    public List<JobVO> getJobsWithoutSchedules() {
        return jobDAO.findJobsWithoutSchedules();
    }

    /**
     * Get jobs with recent executions
     * 
     * @param days Number of days to look back
     * @return List of jobs with executions in the specified period
     */
    public List<JobVO> getJobsWithRecentExecutions(int days) {
        return jobDAO.findJobsWithRecentExecutions(days);
    }

    /**
     * Get jobs with failed executions
     * 
     * @param days Number of days to look back
     * @return List of jobs with failed executions in the specified period
     */
    public List<JobVO> getJobsWithFailedExecutions(int days) {
        return jobDAO.findJobsWithFailedExecutions(days);
    }

    /**
     * Get jobs by execution status
     * 
     * @param status The execution status to filter by
     * @return List of jobs with the specified execution status
     */
    public List<JobVO> getJobsByExecutionStatus(String status) {
        return jobDAO.findJobsByExecutionStatus(status);
    }

    /**
     * Get jobs by average execution time range
     * 
     * @param minDurationMs Minimum duration in milliseconds
     * @param maxDurationMs Maximum duration in milliseconds
     * @return List of jobs with average execution time in the specified range
     */
    public List<JobVO> getJobsByAverageExecutionTime(long minDurationMs, long maxDurationMs) {
        return jobDAO.findJobsByAverageExecutionTime(minDurationMs, maxDurationMs);
    }

    /**
     * Get jobs by success rate
     * 
     * @param minSuccessRate Minimum success rate (0.0 to 1.0)
     * @return List of jobs with success rate above the specified threshold
     */
    public List<JobVO> getJobsBySuccessRate(double minSuccessRate) {
        return jobDAO.findJobsBySuccessRate(minSuccessRate);
    }

    /**
     * Search jobs by name or description
     * 
     * @param searchTerm The search term
     * @return List of jobs matching the search criteria
     */
    public List<JobVO> searchJobs(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllJobs();
        }
        
        String term = searchTerm.trim().toLowerCase();
        return jobDAO.findByName(term);
    }

    /**
     * Get jobs created within a date range
     * 
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return List of jobs created within the specified range
     */
    public List<JobVO> getJobsCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return jobDAO.findJobsCreatedBetween(startDate, endDate);
    }

    /**
     * Get jobs by active status
     * 
     * @param isActive The active status to filter by
     * @return List of jobs with the specified active status
     */
    public List<JobVO> getJobsByStatus(Boolean isActive) {
        return jobDAO.findByStatus(isActive);
    }

    // Validation and Health Check Operations

    /**
     * Validate a job configuration
     * 
     * @param job The job to validate
     * @return true if the job is valid, false otherwise
     */
    public boolean validateJob(JobVO job) {
        if (job == null) {
            return false;
        }
        
        // Check required fields
        if (job.getName() == null || job.getName().trim().isEmpty()) {
            return false;
        }
        
        if (job.getJobClass() == null || job.getJobClass().trim().isEmpty()) {
            return false;
        }
        
        // Check name uniqueness (if not updating)
        if (job.getId() == null && jobDAO.existsByName(job.getName())) {
            return false;
        }
        
        // Check job class uniqueness (if not updating)
        if (job.getId() == null && jobDAO.existsByJobClass(job.getJobClass())) {
            return false;
        }
        
        return true;
    }

    /**
     * Check if a job is healthy based on recent executions
     * 
     * @param jobId The ID of the job
     * @return true if the job is healthy, false otherwise
     */
    public boolean isJobHealthy(Long jobId) {
        // Get recent executions (last 7 days)
        List<JobExecutionVO> recentExecutions = jobExecutionDAO.findRecentExecutionsByJobId(jobId, 7);
        
        if (recentExecutions.isEmpty()) {
            return true; // No recent executions, consider healthy
        }
        
        // Check if there are any failed executions
        long failedCount = recentExecutions.stream()
                .filter(exec -> "FAILED".equals(exec.getStatus()))
                .count();
        
        // Consider healthy if failure rate is less than 50%
        return (double) failedCount / recentExecutions.size() < 0.5;
    }

    // Bulk Operations

    /**
     * Activate all jobs in the system
     */
    public void activateAllJobs() {
        List<JobVO> inactiveJobs = jobDAO.findByStatus(false);
        for (JobVO job : inactiveJobs) {
            activateJob(job.getId());
        }
    }

    /**
     * Deactivate all jobs in the system
     */
    public void deactivateAllJobs() {
        List<JobVO> activeJobs = jobDAO.findByStatus(true);
        for (JobVO job : activeJobs) {
            deactivateJob(job.getId());
        }
    }

    /**
     * Execute all active jobs
     */
    public void executeAllActiveJobs() {
        List<JobVO> activeJobs = getActiveJobs();
        for (JobVO job : activeJobs) {
            try {
                executeJob(job.getId());
            } catch (Exception e) {
                // Log error but continue with other jobs
                System.err.println("Failed to execute job " + job.getId() + ": " + e.getMessage());
            }
        }
    }
} 
package com.home.schedule.api.controller;

import com.home.schedule.api.service.JobService;
import com.home.schedule.api.vo.JobVO;
import com.home.schedule.api.vo.JobExecutionVO;
import com.home.schedule.api.vo.ScheduleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for job management operations.
 * 
 * This controller provides comprehensive REST API endpoints for job-related
 * operations including CRUD operations, job execution, scheduling, and management.
 * It handles HTTP requests and responses, delegates business logic to the service
 * layer, and provides proper error handling and status codes.
 * 
 * Features:
 * - Complete REST API for job management
 * - CRUD operations for jobs
 * - Job execution and scheduling endpoints
 * - Search and filtering capabilities
 * - Statistics and analytics endpoints
 * - Bulk operations support
 * - Health check endpoints
 * 
 * API Endpoints:
 * - GET /api/jobs - Retrieve all jobs
 * - POST /api/jobs - Create a new job
 * - PUT /api/jobs/{id} - Update an existing job
 * - DELETE /api/jobs/{id} - Delete a job
 * - POST /api/jobs/{id}/execute - Execute a job
 * - GET /api/jobs/{id}/executions - Get job executions
 * - POST /api/jobs/{jobId}/schedules/{scheduleId} - Schedule a job
 * 
 * Error Handling:
 * - Proper HTTP status codes (200, 201, 400, 404, 500)
 * - Exception handling with appropriate responses
 * - Validation error responses
 * 
 * Security:
 * - CORS enabled for cross-origin requests
 * - Input validation and sanitization
 * - Proper error message handling
 * 
 * @author Job Scheduler Team
 * @version 1.0.0
 * @since 2024
 */
@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "*")
@Tag(name = "Job Management", description = "APIs for managing jobs, executions, and scheduling")
public class JobController {

    /**
     * Service layer for job operations
     */
    @Autowired
    private JobService jobService;

    // Job CRUD Operations

    /**
     * Retrieve all jobs from the system
     * 
     * @return ResponseEntity containing list of all jobs
     * @apiNote GET /api/jobs
     */
    @Operation(
        summary = "Get all jobs",
        description = "Retrieve a list of all jobs in the system, ordered by creation date (newest first)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved jobs",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = JobVO.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<JobVO>> getAllJobs() {
        try {
            List<JobVO> jobs = jobService.getAllJobs();
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieve all active jobs from the system
     * 
     * @return ResponseEntity containing list of active jobs
     * @apiNote GET /api/jobs/active
     */
    @GetMapping("/active")
    public ResponseEntity<List<JobVO>> getActiveJobs() {
        try {
            List<JobVO> jobs = jobService.getActiveJobs();
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieve a specific job by ID
     * 
     * @param id The job ID
     * @return ResponseEntity containing the job or 404 if not found
     * @apiNote GET /api/jobs/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<JobVO> getJobById(@PathVariable Long id) {
        try {
            Optional<JobVO> job = jobService.getJobById(id);
            return job.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Search jobs by name or description
     * 
     * @param term The search term
     * @return ResponseEntity containing list of matching jobs
     * @apiNote GET /api/jobs/search?term={searchTerm}
     */
    @GetMapping("/search")
    public ResponseEntity<List<JobVO>> searchJobs(@RequestParam String term) {
        try {
            List<JobVO> jobs = jobService.searchJobs(term);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieve jobs by name
     * 
     * @param name The job name to search for
     * @return ResponseEntity containing list of jobs with matching names
     * @apiNote GET /api/jobs/name/{name}
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<List<JobVO>> getJobsByName(@PathVariable String name) {
        try {
            List<JobVO> jobs = jobService.getJobsByName(name);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieve jobs by job class
     * 
     * @param jobClass The job class to search for
     * @return ResponseEntity containing list of jobs with matching job class
     * @apiNote GET /api/jobs/class/{jobClass}
     */
    @GetMapping("/class/{jobClass}")
    public ResponseEntity<List<JobVO>> getJobsByClass(@PathVariable String jobClass) {
        try {
            List<JobVO> jobs = jobService.getJobsByJobClass(jobClass);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieve jobs created by a specific user
     * 
     * @param createdBy The user ID who created the jobs
     * @return ResponseEntity containing list of jobs created by the user
     * @apiNote GET /api/jobs/creator/{createdBy}
     */
    @GetMapping("/creator/{createdBy}")
    public ResponseEntity<List<JobVO>> getJobsByCreator(@PathVariable Long createdBy) {
        try {
            List<JobVO> jobs = jobService.getJobsByCreator(createdBy);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create a new job
     * 
     * @param job The job data to create
     * @return ResponseEntity containing the created job
     * @apiNote POST /api/jobs
     */
    @Operation(
        summary = "Create a new job",
        description = "Create a new job with the provided data. Job names and classes must be unique."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Job created successfully",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = JobVO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid job data or duplicate name/class"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<JobVO> createJob(
        @Parameter(description = "Job data to create", required = true,
            content = @Content(examples = {
                @ExampleObject(name = "Sample Job", value = "{\n" +
                    "    \"name\": \"Data Backup Job\",\n" +
                    "    \"description\": \"Backup database and files\",\n" +
                    "    \"jobClass\": \"com.home.schedule.jobs.DataBackupJob\",\n" +
                    "    \"parameters\": \"{\\\"backupType\\\": \\\"full\\\", \\\"retentionDays\\\": 30}\",\n" +
                    "    \"isActive\": true,\n" +
                    "    \"createdBy\": 1\n" +
                    "}")
            }))
        @RequestBody JobVO job) {
        try {
            jobService.validateJob(job);
            JobVO createdJob = jobService.createJob(job);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdJob);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update an existing job
     * 
     * @param id The job ID to update
     * @param job The updated job data
     * @return ResponseEntity containing the updated job
     * @apiNote PUT /api/jobs/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<JobVO> updateJob(@PathVariable Long id, @RequestBody JobVO job) {
        try {
            JobVO updatedJob = jobService.updateJob(id, job);
            return ResponseEntity.ok(updatedJob);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete a job
     * 
     * @param id The job ID to delete
     * @return ResponseEntity with no content if successful
     * @apiNote DELETE /api/jobs/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        try {
            boolean deleted = jobService.deleteJob(id);
            return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Activate a job
     * 
     * @param id The job ID to activate
     * @return ResponseEntity with no content if successful
     * @apiNote PATCH /api/jobs/{id}/activate
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateJob(@PathVariable Long id) {
        try {
            boolean activated = jobService.activateJob(id);
            return activated ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Deactivate a job
     * 
     * @param id The job ID to deactivate
     * @return ResponseEntity with no content if successful
     * @apiNote PATCH /api/jobs/{id}/deactivate
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateJob(@PathVariable Long id) {
        try {
            boolean deactivated = jobService.deactivateJob(id);
            return deactivated ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update job parameters
     * 
     * @param id The job ID
     * @param parameters The new parameters (JSON string)
     * @return ResponseEntity with no content if successful
     * @apiNote PATCH /api/jobs/{id}/parameters
     */
    @PatchMapping("/{id}/parameters")
    public ResponseEntity<Void> updateJobParameters(@PathVariable Long id, @RequestBody String parameters) {
        try {
            boolean updated = jobService.updateJobParameters(id, parameters);
            return updated ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Job Execution Operations



    /**
     * Execute a job with custom parameters
     * 
     * @param id The job ID to execute
     * @param parameters Custom parameters for the execution (JSON string)
     * @return ResponseEntity containing the job execution
     * @apiNote POST /api/jobs/{id}/execute
     */
    @Operation(
        summary = "Execute a job",
        description = "Execute a job with optional custom parameters. The job must be active to execute."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Job execution started successfully",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = JobExecutionVO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid job ID or job is inactive"),
        @ApiResponse(responseCode = "404", description = "Job not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{id}/execute")
    public ResponseEntity<JobExecutionVO> executeJob(
        @Parameter(description = "Job ID to execute", required = true) @PathVariable Long id, 
        @Parameter(description = "Custom parameters for execution (JSON string)", required = false,
            content = @Content(examples = {
                @ExampleObject(name = "Sample Parameters", value = "{\n" +
                    "    \"backupType\": \"incremental\",\n" +
                    "    \"targetPath\": \"/backup/daily\"\n" +
                    "}")
            }))
        @RequestBody(required = false) String parameters) {
        try {
            JobExecutionVO execution = jobService.executeJob(id, parameters);
            return ResponseEntity.status(HttpStatus.CREATED).body(execution);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all executions for a specific job
     * 
     * @param id The job ID
     * @return ResponseEntity containing list of job executions
     * @apiNote GET /api/jobs/{id}/executions
     */
    @GetMapping("/{id}/executions")
    public ResponseEntity<List<JobExecutionVO>> getJobExecutions(@PathVariable Long id) {
        try {
            List<JobExecutionVO> executions = jobService.getJobExecutions(id);
            return ResponseEntity.ok(executions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get job executions by status
     * 
     * @param id The job ID
     * @param status The execution status to filter by
     * @return ResponseEntity containing list of job executions with the specified status
     * @apiNote GET /api/jobs/{id}/executions/status/{status}
     */
    @GetMapping("/{id}/executions/status/{status}")
    public ResponseEntity<List<JobExecutionVO>> getJobExecutionsByStatus(@PathVariable Long id, @PathVariable String status) {
        try {
            List<JobExecutionVO> executions = jobService.getJobExecutionsByStatus(id, status);
            return ResponseEntity.ok(executions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Cancel a running job execution
     * 
     * @param executionId The execution ID to cancel
     * @return ResponseEntity with no content if successful
     * @apiNote DELETE /api/jobs/executions/{executionId}
     */
    @DeleteMapping("/executions/{executionId}")
    public ResponseEntity<Void> cancelJobExecution(@PathVariable Long executionId) {
        try {
            boolean cancelled = jobService.cancelJobExecution(executionId);
            return cancelled ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Job Scheduling Operations

    /**
     * Schedule a job with a specific schedule
     * 
     * @param jobId The job ID
     * @param scheduleId The schedule ID
     * @return ResponseEntity with no content if successful
     * @apiNote POST /api/jobs/{jobId}/schedules/{scheduleId}
     */
    @PostMapping("/{jobId}/schedules/{scheduleId}")
    public ResponseEntity<Void> scheduleJob(@PathVariable Long jobId, @PathVariable Long scheduleId) {
        try {
            boolean scheduled = jobService.scheduleJob(jobId, scheduleId);
            return scheduled ? ResponseEntity.noContent().build() : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Remove a job from a specific schedule
     * 
     * @param jobId The job ID
     * @param scheduleId The schedule ID
     * @return ResponseEntity with no content if successful
     * @apiNote DELETE /api/jobs/{jobId}/schedules/{scheduleId}
     */
    @DeleteMapping("/{jobId}/schedules/{scheduleId}")
    public ResponseEntity<Void> unscheduleJob(@PathVariable Long jobId, @PathVariable Long scheduleId) {
        try {
            boolean unscheduled = jobService.unscheduleJob(jobId, scheduleId);
            return unscheduled ? ResponseEntity.noContent().build() : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all schedules associated with a job
     * 
     * @param id The job ID
     * @return ResponseEntity containing list of schedules associated with the job
     * @apiNote GET /api/jobs/{id}/schedules
     */
    @GetMapping("/{id}/schedules")
    public ResponseEntity<List<ScheduleVO>> getJobSchedules(@PathVariable Long id) {
        try {
            List<ScheduleVO> schedules = jobService.getJobSchedules(id);
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Check if a job has any active schedules
     * 
     * @param id The job ID
     * @return ResponseEntity containing boolean indicating if job is scheduled
     * @apiNote GET /api/jobs/{id}/scheduled
     */
    @GetMapping("/{id}/scheduled")
    public ResponseEntity<Boolean> isJobScheduled(@PathVariable Long id) {
        try {
            boolean scheduled = jobService.isJobScheduled(id);
            return ResponseEntity.ok(scheduled);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Statistics and Analytics Endpoints

    /**
     * Get the total number of jobs in the system
     * 
     * @return ResponseEntity containing the total job count
     * @apiNote GET /api/jobs/stats/count
     */
    @GetMapping("/stats/count")
    public ResponseEntity<Long> getTotalJobCount() {
        try {
            long count = jobService.getTotalJobCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get the number of active jobs in the system
     * 
     * @return ResponseEntity containing the active job count
     * @apiNote GET /api/jobs/stats/active-count
     */
    @GetMapping("/stats/active-count")
    public ResponseEntity<Long> getActiveJobCount() {
        try {
            long count = jobService.getActiveJobCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get jobs that have associated schedules
     * 
     * @return ResponseEntity containing list of jobs with schedules
     * @apiNote GET /api/jobs/with-schedules
     */
    @GetMapping("/with-schedules")
    public ResponseEntity<List<JobVO>> getJobsWithSchedules() {
        try {
            List<JobVO> jobs = jobService.getJobsWithSchedules();
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get jobs that have no associated schedules
     * 
     * @return ResponseEntity containing list of jobs without schedules
     * @apiNote GET /api/jobs/without-schedules
     */
    @GetMapping("/without-schedules")
    public ResponseEntity<List<JobVO>> getJobsWithoutSchedules() {
        try {
            List<JobVO> jobs = jobService.getJobsWithoutSchedules();
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get jobs with recent executions
     * 
     * @param days Number of days to look back
     * @return ResponseEntity containing list of jobs with recent executions
     * @apiNote GET /api/jobs/recent-executions/{days}
     */
    @GetMapping("/recent-executions/{days}")
    public ResponseEntity<List<JobVO>> getJobsWithRecentExecutions(@PathVariable int days) {
        try {
            List<JobVO> jobs = jobService.getJobsWithRecentExecutions(days);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get jobs with failed executions
     * 
     * @param days Number of days to look back
     * @return ResponseEntity containing list of jobs with failed executions
     * @apiNote GET /api/jobs/failed-executions/{days}
     */
    @GetMapping("/failed-executions/{days}")
    public ResponseEntity<List<JobVO>> getJobsWithFailedExecutions(@PathVariable int days) {
        try {
            List<JobVO> jobs = jobService.getJobsWithFailedExecutions(days);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get jobs by execution status
     * 
     * @param status The execution status to filter by
     * @return ResponseEntity containing list of jobs with the specified execution status
     * @apiNote GET /api/jobs/execution-status/{status}
     */
    @GetMapping("/execution-status/{status}")
    public ResponseEntity<List<JobVO>> getJobsByExecutionStatus(@PathVariable String status) {
        try {
            List<JobVO> jobs = jobService.getJobsByExecutionStatus(status);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get jobs by average execution time range
     * 
     * @param minDurationMs Minimum duration in milliseconds
     * @param maxDurationMs Maximum duration in milliseconds
     * @return ResponseEntity containing list of jobs with average execution time in the specified range
     * @apiNote GET /api/jobs/execution-time?minDurationMs={min}&maxDurationMs={max}
     */
    @GetMapping("/execution-time")
    public ResponseEntity<List<JobVO>> getJobsByExecutionTime(
            @RequestParam long minDurationMs,
            @RequestParam long maxDurationMs) {
        try {
            List<JobVO> jobs = jobService.getJobsByAverageExecutionTime(minDurationMs, maxDurationMs);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get jobs by success rate
     * 
     * @param minSuccessRate Minimum success rate (0.0 to 1.0)
     * @return ResponseEntity containing list of jobs with success rate above the specified threshold
     * @apiNote GET /api/jobs/success-rate?minSuccessRate={rate}
     */
    @GetMapping("/success-rate")
    public ResponseEntity<List<JobVO>> getJobsBySuccessRate(@RequestParam double minSuccessRate) {
        try {
            List<JobVO> jobs = jobService.getJobsBySuccessRate(minSuccessRate);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get jobs created within a date range
     * 
     * @param startDate The start date (ISO format)
     * @param endDate The end date (ISO format)
     * @return ResponseEntity containing list of jobs created within the specified range
     * @apiNote GET /api/jobs/created-between?startDate={date}&endDate={date}
     */
    @GetMapping("/created-between")
    public ResponseEntity<List<JobVO>> getJobsCreatedBetween(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            List<JobVO> jobs = jobService.getJobsCreatedBetween(start, end);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get jobs by active status
     * 
     * @param isActive The active status to filter by
     * @return ResponseEntity containing list of jobs with the specified active status
     * @apiNote GET /api/jobs/status/{isActive}
     */
    @GetMapping("/status/{isActive}")
    public ResponseEntity<List<JobVO>> getJobsByStatus(@PathVariable Boolean isActive) {
        try {
            List<JobVO> jobs = jobService.getJobsByStatus(isActive);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Health Check Endpoints

    /**
     * Check if a job is healthy based on recent executions
     * 
     * @param id The job ID
     * @return ResponseEntity containing boolean indicating if job is healthy
     * @apiNote GET /api/jobs/{id}/health
     */
    @GetMapping("/{id}/health")
    public ResponseEntity<Boolean> isJobHealthy(@PathVariable Long id) {
        try {
            boolean healthy = jobService.isJobHealthy(id);
            return ResponseEntity.ok(healthy);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Bulk Operations

    /**
     * Activate all jobs in the system
     * 
     * @return ResponseEntity with no content if successful
     * @apiNote POST /api/jobs/bulk/activate
     */
    @PostMapping("/bulk/activate")
    public ResponseEntity<Void> activateAllJobs() {
        try {
            jobService.activateAllJobs();
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Deactivate all jobs in the system
     * 
     * @return ResponseEntity with no content if successful
     * @apiNote POST /api/jobs/bulk/deactivate
     */
    @PostMapping("/bulk/deactivate")
    public ResponseEntity<Void> deactivateAllJobs() {
        try {
            jobService.deactivateAllJobs();
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Execute all active jobs
     * 
     * @return ResponseEntity with no content if successful
     * @apiNote POST /api/jobs/bulk/execute
     */
    @PostMapping("/bulk/execute")
    public ResponseEntity<Void> executeAllActiveJobs() {
        try {
            jobService.executeAllActiveJobs();
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 
package com.home.schedule.api.service;

import com.home.schedule.api.dao.JobDAO;
import com.home.schedule.api.dao.JobExecutionDAO;
import com.home.schedule.api.dao.JobScheduleDAO;
import com.home.schedule.api.vo.JobExecutionVO;
import com.home.schedule.api.vo.JobVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class JobSchedulerService {

    @Autowired
    private JobDAO jobDAO;

    @Autowired
    private JobExecutionDAO jobExecutionDAO;

    @Autowired
    private JobScheduleDAO jobScheduleDAO;

    @Autowired
    private JobExecutionService jobExecutionService;

    /**
     * Schedule a job with a specific schedule
     */
    public boolean scheduleJob(Long jobId, Long scheduleId) {
        try {
            // Check if job and schedule exist
            if (jobDAO.findById(jobId).isEmpty()) {
                return false;
            }

            // Create job-schedule mapping
            return jobScheduleDAO.createJobSchedule(jobId, scheduleId);
        } catch (Exception e) {
            // Log error
            System.err.println("Failed to schedule job " + jobId + " with schedule " + scheduleId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Unschedule a job from a specific schedule
     */
    public boolean unscheduleJob(Long jobId, Long scheduleId) {
        try {
            return jobScheduleDAO.deleteJobSchedule(jobId, scheduleId);
        } catch (Exception e) {
            System.err.println("Failed to unschedule job " + jobId + " from schedule " + scheduleId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Execute a job asynchronously
     */
    @Async
    public CompletableFuture<JobExecutionVO> executeJobAsync(JobExecutionVO execution) {
        try {
            // Update status to RUNNING
            jobExecutionDAO.startExecution(execution.getId());
            execution.setStatus("RUNNING");
            execution.setStartTime(LocalDateTime.now());

            // Execute the job
            JobExecutionVO result = jobExecutionService.executeJob(execution);

            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            // Handle execution failure
            jobExecutionDAO.failExecution(execution.getId(), e.getMessage(), 
                getStackTrace(e), System.currentTimeMillis() - execution.getStartTime().toInstant(java.time.ZoneOffset.UTC).toEpochMilli());
            
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Cancel a specific job execution
     */
    public boolean cancelJobExecution(Long executionId) {
        try {
            return jobExecutionDAO.cancelExecution(executionId);
        } catch (Exception e) {
            System.err.println("Failed to cancel job execution " + executionId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Cancel all executions for a specific job
     */
    public boolean cancelJobExecutions(Long jobId) {
        try {
            List<JobExecutionVO> runningExecutions = jobExecutionDAO.findRunningExecutionsByJobId(jobId);
            for (JobExecutionVO execution : runningExecutions) {
                jobExecutionDAO.cancelExecution(execution.getId());
            }
            return true;
        } catch (Exception e) {
            System.err.println("Failed to cancel job executions for job " + jobId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Pause a job (stop scheduling new executions)
     */
    public boolean pauseJob(Long jobId) {
        try {
            return jobScheduleDAO.deactivateJobSchedules(jobId);
        } catch (Exception e) {
            System.err.println("Failed to pause job " + jobId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Resume a job (start scheduling new executions)
     */
    public boolean resumeJob(Long jobId) {
        try {
            return jobScheduleDAO.activateJobSchedules(jobId);
        } catch (Exception e) {
            System.err.println("Failed to resume job " + jobId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Get next execution time for a job
     */
    public LocalDateTime getNextExecutionTime(Long jobId) {
        try {
            // This would typically involve cron expression parsing
            // For now, return a placeholder
            return LocalDateTime.now().plusHours(1);
        } catch (Exception e) {
            System.err.println("Failed to get next execution time for job " + jobId + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Get previous execution time for a job
     */
    public LocalDateTime getPreviousExecutionTime(Long jobId) {
        try {
            List<JobExecutionVO> recentExecutions = jobExecutionDAO.findRecentExecutionsByJobId(jobId, 1);
            if (!recentExecutions.isEmpty()) {
                return recentExecutions.get(0).getStartTime();
            }
            return null;
        } catch (Exception e) {
            System.err.println("Failed to get previous execution time for job " + jobId + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Check if a job is currently scheduled
     */
    public boolean isJobScheduled(Long jobId) {
        try {
            return jobScheduleDAO.hasActiveSchedules(jobId);
        } catch (Exception e) {
            System.err.println("Failed to check if job " + jobId + " is scheduled: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all scheduled jobs
     */
    public List<JobVO> getScheduledJobs() {
        try {
            return jobDAO.findJobsWithSchedules();
        } catch (Exception e) {
            System.err.println("Failed to get scheduled jobs: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Get all unscheduled jobs
     */
    public List<JobVO> getUnscheduledJobs() {
        try {
            return jobDAO.findJobsWithoutSchedules();
        } catch (Exception e) {
            System.err.println("Failed to get unscheduled jobs: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Validate cron expression
     */
    public boolean validateCronExpression(String cronExpression) {
        try {
            // Basic validation - in a real implementation, you'd use a proper cron parser
            if (cronExpression == null || cronExpression.trim().isEmpty()) {
                return false;
            }
            
            String[] parts = cronExpression.split("\\s+");
            return parts.length >= 6; // Standard cron has 6-7 parts
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get execution statistics for a job
     */
    public JobExecutionStats getJobExecutionStats(Long jobId) {
        try {
            long totalExecutions = jobExecutionDAO.countByJobId(jobId);
            long completedExecutions = jobExecutionDAO.countByStatus("COMPLETED");
            long failedExecutions = jobExecutionDAO.countByStatus("FAILED");
            long runningExecutions = jobExecutionDAO.countByStatus("RUNNING");

            return new JobExecutionStats(totalExecutions, completedExecutions, failedExecutions, runningExecutions);
        } catch (Exception e) {
            System.err.println("Failed to get execution stats for job " + jobId + ": " + e.getMessage());
            return new JobExecutionStats(0, 0, 0, 0);
        }
    }

    /**
     * Clean up old job executions
     */
    public void cleanupOldExecutions(int daysToKeep) {
        try {
            jobExecutionDAO.cleanupOldExecutions(daysToKeep);
        } catch (Exception e) {
            System.err.println("Failed to cleanup old executions: " + e.getMessage());
        }
    }

    private String getStackTrace(Exception e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Inner class for job execution statistics
     */
    public static class JobExecutionStats {
        private final long totalExecutions;
        private final long completedExecutions;
        private final long failedExecutions;
        private final long runningExecutions;

        public JobExecutionStats(long totalExecutions, long completedExecutions, long failedExecutions, long runningExecutions) {
            this.totalExecutions = totalExecutions;
            this.completedExecutions = completedExecutions;
            this.failedExecutions = failedExecutions;
            this.runningExecutions = runningExecutions;
        }

        public long getTotalExecutions() { return totalExecutions; }
        public long getCompletedExecutions() { return completedExecutions; }
        public long getFailedExecutions() { return failedExecutions; }
        public long getRunningExecutions() { return runningExecutions; }
        public double getSuccessRate() { 
            return totalExecutions > 0 ? (double) completedExecutions / totalExecutions * 100 : 0; 
        }
    }
} 
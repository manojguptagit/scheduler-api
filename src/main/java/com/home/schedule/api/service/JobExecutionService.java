package com.home.schedule.api.service;

import com.home.schedule.api.dao.JobDAO;
import com.home.schedule.api.dao.JobExecutionDAO;
import com.home.schedule.api.vo.JobExecutionVO;
import com.home.schedule.api.vo.JobVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class JobExecutionService {

    @Autowired
    private JobDAO jobDAO;

    @Autowired
    private JobExecutionDAO jobExecutionDAO;

    public JobExecutionVO executeJob(JobExecutionVO execution) {
        try {
            // Get job details
            JobVO job = jobDAO.findById(execution.getJobId()).orElse(null);
            if (job == null) {
                throw new IllegalArgumentException("Job not found: " + execution.getJobId());
            }

            // Execute the job based on its class
            String result = executeJobByClass(job.getJobClass(), execution.getParameters());
            
            // Calculate duration
            long durationMs = System.currentTimeMillis() - execution.getStartTime().toInstant(java.time.ZoneOffset.UTC).toEpochMilli();
            
            // Update execution record
            jobExecutionDAO.completeExecution(execution.getId(), result, durationMs);
            
            execution.setStatus("COMPLETED");
            execution.setEndTime(LocalDateTime.now());
            execution.setDurationMs(durationMs);
            execution.setResultMessage(result);
            
            return execution;
            
        } catch (Exception e) {
            // Handle execution failure
            long durationMs = System.currentTimeMillis() - execution.getStartTime().toInstant(java.time.ZoneOffset.UTC).toEpochMilli();
            jobExecutionDAO.failExecution(execution.getId(), e.getMessage(), getStackTrace(e), durationMs);
            
            execution.setStatus("FAILED");
            execution.setEndTime(LocalDateTime.now());
            execution.setDurationMs(durationMs);
            execution.setErrorMessage(e.getMessage());
            execution.setStackTrace(getStackTrace(e));
            
            return execution;
        }
    }

    private String executeJobByClass(String jobClass, String parameters) {
        // This is a placeholder implementation
        // In a real application, you would use reflection or a job registry to execute the actual job
        
        try {
            // Simulate job execution
            Thread.sleep(1000); // Simulate work
            
            // Return success message
            return "Job executed successfully with parameters: " + parameters;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Job execution interrupted", e);
        }
    }

    private String getStackTrace(Exception e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
} 
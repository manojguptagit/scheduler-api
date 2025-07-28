package com.home.schedule.api.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Value Object (VO) representing notification configuration for a job.
 * 
 * This class encapsulates information about how and when to send notifications
 * for job execution events. It maps to the 'job_notifications' table in the
 * database and provides flexible notification management capabilities.
 * 
 * Features:
 * - Notification type configuration (EMAIL, SMS, WEBHOOK, SLACK, etc.)
 * - Recipient management
 * - Template-based notification content
 * - Status management (active/inactive)
 * - Audit trail for notification configuration
 * 
 * Notification Types:
 * - EMAIL: Send email notifications
 * - SMS: Send SMS notifications
 * - WEBHOOK: Send HTTP webhook notifications
 * - SLACK: Send Slack channel notifications
 * - TEAMS: Send Microsoft Teams notifications
 * - CUSTOM: Custom notification implementation
 * 
 * Use Cases:
 * - Alert administrators about job failures
 * - Notify stakeholders about job completion
 * - Send status updates to monitoring systems
 * - Integrate with external notification services
 * 
 * Relationships:
 * - Belongs to a specific job
 * - Defines notification behavior for job events
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
public class JobNotificationVO {
    
    /**
     * Unique identifier for the notification configuration (Primary Key)
     */
    private Long id;
    
    /**
     * ID of the job this notification belongs to (Foreign Key to jobs table)
     */
    private Long jobId;
    
    /**
     * Type of notification to send
     * Values: EMAIL, SMS, WEBHOOK, SLACK, TEAMS, CUSTOM
     */
    private String notificationType;
    
    /**
     * Recipient of the notification
     * Format depends on notification type:
     * - EMAIL: email address
     * - SMS: phone number
     * - WEBHOOK: URL
     * - SLACK: channel name or user ID
     * - TEAMS: channel or user identifier
     */
    private String recipient;
    
    /**
     * Template for the notification message
     * Can include placeholders for dynamic content
     * Examples: "Job {jobName} completed successfully", "Job {jobName} failed with error: {error}"
     */
    private String template;
    
    /**
     * Flag indicating whether this notification is active
     * Inactive notifications are not sent
     */
    private Boolean isActive;
    
    /**
     * Timestamp when the notification configuration was created
     * Automatically set by the database
     */
    private LocalDateTime createdAt;
    
    // Additional fields for convenience - not stored in database
    
    /**
     * The job this notification belongs to
     * Populated when retrieving notification with job details
     */
    private JobVO job;
} 
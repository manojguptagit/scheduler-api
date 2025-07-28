package com.home.schedule.api.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Value Object (VO) representing the history of notification attempts.
 * 
 * This class encapsulates information about notification delivery attempts,
 * including success/failure status and error details. It maps to the
 * 'notification_history' table in the database and provides comprehensive
 * notification tracking and auditing capabilities.
 * 
 * Features:
 * - Notification delivery tracking
 * - Success/failure status monitoring
 * - Error message storage for failed notifications
 * - Timestamp tracking for delivery attempts
 * - Relationship with job executions and notification configurations
 * 
 * Notification Status Values:
 * - PENDING: Notification is queued for delivery
 * - SENT: Notification was successfully delivered
 * - FAILED: Notification delivery failed
 * - RETRY: Notification is being retried
 * - CANCELLED: Notification was cancelled
 * 
 * Use Cases:
 * - Track notification delivery success rates
 * - Debug notification delivery issues
 * - Audit notification history for compliance
 * - Monitor notification system health
 * - Retry failed notifications
 * 
 * Relationships:
 * - Belongs to a specific job execution
 * - References a specific notification configuration
 * - Tracks delivery attempts and results
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
public class NotificationHistoryVO {
    
    /**
     * Unique identifier for the notification history record (Primary Key)
     */
    private Long id;
    
    /**
     * ID of the job execution that triggered this notification (Foreign Key to job_executions table)
     */
    private Long jobExecutionId;
    
    /**
     * ID of the notification configuration used (Foreign Key to job_notifications table)
     */
    private Long notificationId;
    
    /**
     * Status of the notification delivery attempt
     * Values: PENDING, SENT, FAILED, RETRY, CANCELLED
     */
    private String status;
    
    /**
     * Timestamp when the notification was sent
     * Set when notification delivery is attempted
     */
    private LocalDateTime sentAt;
    
    /**
     * Error message if notification delivery failed
     * Provides details about why the notification failed
     */
    private String errorMessage;
    
    /**
     * Timestamp when the notification history record was created
     * Automatically set by the database
     */
    private LocalDateTime createdAt;
    
    // Additional fields for convenience - not stored in database
    
    /**
     * The job execution that triggered this notification
     * Populated when retrieving notification history with execution details
     */
    private JobExecutionVO jobExecution;
    
    /**
     * The notification configuration used for this delivery attempt
     * Populated when retrieving notification history with notification details
     */
    private JobNotificationVO notification;
} 
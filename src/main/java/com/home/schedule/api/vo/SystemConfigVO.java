package com.home.schedule.api.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Value Object (VO) representing system configuration settings.
 * 
 * This class encapsulates application-wide configuration settings that can be
 * modified at runtime without requiring application restart. It maps to the
 * 'system_config' table in the database and provides flexible configuration management.
 * 
 * Features:
 * - Runtime configuration management
 * - Key-value pair storage
 * - Encrypted value support for sensitive data
 * - Configuration description and documentation
 * - Audit trail for configuration changes
 * 
 * Configuration Types:
 * - Application settings (timeouts, limits, thresholds)
 * - Feature flags and toggles
 * - Integration settings (API endpoints, credentials)
 * - Performance tuning parameters
 * - Security and authentication settings
 * 
 * Use Cases:
 * - Dynamic application configuration
 * - Feature flag management
 * - Environment-specific settings
 * - Runtime parameter tuning
 * - Secure credential storage
 * - Configuration versioning and rollback
 * 
 * Security Features:
 * - Encrypted value storage for sensitive data
 * - Audit trail for all configuration changes
 * - Access control for configuration modification
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
public class SystemConfigVO {
    
    /**
     * Unique identifier for the configuration (Primary Key)
     */
    private Long id;
    
    /**
     * Configuration key (unique identifier)
     * Used to retrieve and update configuration values
     * Examples: "job.execution.timeout", "notification.email.enabled", "max.concurrent.jobs"
     */
    private String configKey;
    
    /**
     * Configuration value
     * Can be any string value (JSON, boolean, number, etc.)
     * May be encrypted if isEncrypted is true
     */
    private String configValue;
    
    /**
     * Human-readable description of the configuration
     * Explains what the configuration controls and how to use it
     */
    private String description;
    
    /**
     * Flag indicating whether the configuration value is encrypted
     * Encrypted values are automatically encrypted/decrypted by the system
     * Used for sensitive data like passwords, API keys, etc.
     */
    private Boolean isEncrypted;
    
    /**
     * Timestamp when the configuration was created
     * Automatically set by the database
     */
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the configuration was last updated
     * Automatically updated by the database on each modification
     */
    private LocalDateTime updatedAt;
} 
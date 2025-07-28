-- Job Scheduling Application Database Schema

-- Users table for authentication and authorization
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Job definitions table
CREATE TABLE jobs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    job_class VARCHAR(255) NOT NULL,
    parameters TEXT, -- JSON string for job parameters
    is_active BOOLEAN DEFAULT TRUE,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- Schedule definitions table
CREATE TABLE schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    cron_expression VARCHAR(100) NOT NULL,
    timezone VARCHAR(50) DEFAULT 'UTC',
    is_active BOOLEAN DEFAULT TRUE,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- Job-Schedule mapping table (many-to-many relationship)
CREATE TABLE job_schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_id BIGINT NOT NULL,
    schedule_id BIGINT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    FOREIGN KEY (schedule_id) REFERENCES schedules(id) ON DELETE CASCADE,
    UNIQUE (job_id, schedule_id)
);

-- Job execution history table
CREATE TABLE job_executions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_id BIGINT NOT NULL,
    schedule_id BIGINT,
    execution_id VARCHAR(100) UNIQUE NOT NULL,
    status ENUM('PENDING', 'RUNNING', 'COMPLETED', 'FAILED', 'CANCELLED') NOT NULL,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    duration_ms BIGINT,
    result_message TEXT,
    error_message TEXT,
    stack_trace TEXT,
    parameters TEXT, -- JSON string for execution parameters
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (job_id) REFERENCES jobs(id),
    FOREIGN KEY (schedule_id) REFERENCES schedules(id)
);

-- Job execution steps table (for complex jobs with multiple steps)
CREATE TABLE job_execution_steps (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_execution_id BIGINT NOT NULL,
    step_name VARCHAR(100) NOT NULL,
    step_order INT NOT NULL,
    status ENUM('PENDING', 'RUNNING', 'COMPLETED', 'FAILED', 'SKIPPED') NOT NULL,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    duration_ms BIGINT,
    result_message TEXT,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (job_execution_id) REFERENCES job_executions(id) ON DELETE CASCADE
);

-- Job dependencies table (for jobs that depend on other jobs)
CREATE TABLE job_dependencies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dependent_job_id BIGINT NOT NULL,
    prerequisite_job_id BIGINT NOT NULL,
    dependency_type ENUM('BLOCKING', 'NON_BLOCKING') DEFAULT 'BLOCKING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (dependent_job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    FOREIGN KEY (prerequisite_job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    UNIQUE (dependent_job_id, prerequisite_job_id)
);

-- Job notifications table
CREATE TABLE job_notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_id BIGINT NOT NULL,
    notification_type ENUM('EMAIL', 'SMS', 'WEBHOOK', 'SLACK') NOT NULL,
    recipient VARCHAR(255) NOT NULL,
    template VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE
);

-- Notification history table
CREATE TABLE notification_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_execution_id BIGINT NOT NULL,
    notification_id BIGINT NOT NULL,
    status ENUM('PENDING', 'SENT', 'FAILED') NOT NULL,
    sent_at TIMESTAMP,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (job_execution_id) REFERENCES job_executions(id),
    FOREIGN KEY (notification_id) REFERENCES job_notifications(id)
);

-- Job locks table (for preventing concurrent execution of the same job)
CREATE TABLE job_locks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_id BIGINT NOT NULL UNIQUE,
    lock_holder VARCHAR(100) NOT NULL,
    lock_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE
);

-- Job statistics table (for performance monitoring)
CREATE TABLE job_statistics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_id BIGINT NOT NULL,
    date DATE NOT NULL,
    total_executions INT DEFAULT 0,
    successful_executions INT DEFAULT 0,
    failed_executions INT DEFAULT 0,
    total_duration_ms BIGINT DEFAULT 0,
    avg_duration_ms BIGINT DEFAULT 0,
    min_duration_ms BIGINT,
    max_duration_ms BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    UNIQUE (job_id, date)
);

-- System configuration table
CREATE TABLE system_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT,
    description TEXT,
    is_encrypted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Audit log table
CREATE TABLE audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT,
    old_values TEXT,
    new_values TEXT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create indexes for better performance
CREATE INDEX idx_jobs_active ON jobs(is_active);
CREATE INDEX idx_schedules_active ON schedules(is_active);
CREATE INDEX idx_job_executions_status ON job_executions(status);
CREATE INDEX idx_job_executions_job_id ON job_executions(job_id);
CREATE INDEX idx_job_executions_start_time ON job_executions(start_time);
CREATE INDEX idx_job_execution_steps_execution_id ON job_execution_steps(job_execution_id);
CREATE INDEX idx_job_dependencies_dependent ON job_dependencies(dependent_job_id);
CREATE INDEX idx_job_dependencies_prerequisite ON job_dependencies(prerequisite_job_id);
CREATE INDEX idx_job_locks_expires ON job_locks(expires_at);
CREATE INDEX idx_job_statistics_job_date ON job_statistics(job_id, date);
CREATE INDEX idx_audit_log_entity ON audit_log(entity_type, entity_id);
CREATE INDEX idx_audit_log_created_at ON audit_log(created_at); 
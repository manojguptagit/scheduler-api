-- Sample data for Job Scheduling Application

-- Insert default admin user (password: admin123)
INSERT INTO users (username, email, password_hash, first_name, last_name, is_active) 
VALUES ('admin', 'admin@scheduler.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Admin', 'User', true);

-- Insert sample jobs
INSERT INTO jobs (name, description, job_class, parameters, is_active, created_by) VALUES
('Data Backup Job', 'Backup database and files to remote storage', 'com.home.schedule.jobs.DataBackupJob', '{"backupType": "full", "retentionDays": 30}', true, 1),
('Email Report Job', 'Generate and send daily email reports', 'com.home.schedule.jobs.EmailReportJob', '{"reportType": "daily", "recipients": ["reports@company.com"]}', true, 1),
('System Cleanup Job', 'Clean up temporary files and logs', 'com.home.schedule.jobs.SystemCleanupJob', '{"cleanupType": "logs", "maxAgeDays": 7}', true, 1),
('Data Sync Job', 'Synchronize data between systems', 'com.home.schedule.jobs.DataSyncJob', '{"sourceSystem": "CRM", "targetSystem": "ERP"}', true, 1),
('Health Check Job', 'Monitor system health and send alerts', 'com.home.schedule.jobs.HealthCheckJob', '{"checkInterval": 300, "alertThreshold": 0.9}', true, 1);

-- Insert sample schedules
INSERT INTO schedules (name, description, cron_expression, timezone, is_active, created_by) VALUES
('Daily at 2 AM', 'Run jobs daily at 2:00 AM', '0 0 2 * * ?', 'UTC', true, 1),
('Every Hour', 'Run jobs every hour', '0 0 * * * ?', 'UTC', true, 1),
('Every 15 Minutes', 'Run jobs every 15 minutes', '0 */15 * * * ?', 'UTC', true, 1),
('Weekdays at 9 AM', 'Run jobs on weekdays at 9:00 AM', '0 0 9 ? * MON-FRI', 'UTC', true, 1),
('Monthly on 1st', 'Run jobs on the 1st of every month', '0 0 0 1 * ?', 'UTC', true, 1),
('Every 5 Minutes', 'Run jobs every 5 minutes', '0 */5 * * * ?', 'UTC', true, 1);

-- Link jobs to schedules
INSERT INTO job_schedules (job_id, schedule_id, is_active) VALUES
(1, 1, true),  -- Data Backup Job -> Daily at 2 AM
(2, 4, true),  -- Email Report Job -> Weekdays at 9 AM
(3, 2, true),  -- System Cleanup Job -> Every Hour
(4, 3, true),  -- Data Sync Job -> Every 15 Minutes
(5, 6, true);  -- Health Check Job -> Every 5 Minutes

-- Insert sample job dependencies
INSERT INTO job_dependencies (dependent_job_id, prerequisite_job_id, dependency_type) VALUES
(2, 1, 'BLOCKING'),    -- Email Report depends on Data Backup
(4, 3, 'NON_BLOCKING'); -- Data Sync can run independently of System Cleanup

-- Insert sample notifications
INSERT INTO job_notifications (job_id, notification_type, recipient, template, is_active) VALUES
(1, 'EMAIL', 'admin@scheduler.com', 'backup-completion', true),
(2, 'EMAIL', 'reports@company.com', 'daily-report', true),
(5, 'WEBHOOK', 'https://alerts.company.com/webhook', 'health-alert', true);

-- Insert sample system configuration
INSERT INTO system_config (config_key, config_value, description, is_encrypted) VALUES
('max.concurrent.jobs', '10', 'Maximum number of jobs that can run concurrently', false),
('job.timeout.minutes', '30', 'Default timeout for job execution in minutes', false),
('retention.days', '90', 'Number of days to retain job execution history', false),
('notification.enabled', 'true', 'Enable/disable job notifications', false),
('smtp.host', 'smtp.company.com', 'SMTP server host for email notifications', false),
('smtp.port', '587', 'SMTP server port', false),
('smtp.username', 'noreply@company.com', 'SMTP username', false),
('smtp.password', 'encrypted_password_here', 'SMTP password', true);

-- Insert sample job statistics (for demonstration)
INSERT INTO job_statistics (job_id, date, total_executions, successful_executions, failed_executions, total_duration_ms, avg_duration_ms, min_duration_ms, max_duration_ms) VALUES
(1, DATEADD('DAY', -1, CURRENT_DATE), 1, 1, 0, 45000, 45000, 45000, 45000),
(2, DATEADD('DAY', -1, CURRENT_DATE), 1, 1, 0, 12000, 12000, 12000, 12000),
(3, DATEADD('DAY', -1, CURRENT_DATE), 24, 23, 1, 180000, 7500, 5000, 15000),
(4, DATEADD('DAY', -1, CURRENT_DATE), 96, 95, 1, 480000, 5000, 3000, 8000),
(5, DATEADD('DAY', -1, CURRENT_DATE), 288, 287, 1, 144000, 500, 300, 1000); 
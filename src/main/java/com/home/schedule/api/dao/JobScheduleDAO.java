package com.home.schedule.api.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JobScheduleDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean createJobSchedule(Long jobId, Long scheduleId) {
        String sql = "INSERT INTO job_schedules (job_id, schedule_id, is_active) VALUES (?, ?, true)";
        try {
            int rowsAffected = jdbcTemplate.update(sql, jobId, scheduleId);
            return rowsAffected > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteJobSchedule(Long jobId, Long scheduleId) {
        String sql = "DELETE FROM job_schedules WHERE job_id = ? AND schedule_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, jobId, scheduleId);
        return rowsAffected > 0;
    }

    public boolean activateJobSchedules(Long jobId) {
        String sql = "UPDATE job_schedules SET is_active = true WHERE job_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, jobId);
        return rowsAffected > 0;
    }

    public boolean deactivateJobSchedules(Long jobId) {
        String sql = "UPDATE job_schedules SET is_active = false WHERE job_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, jobId);
        return rowsAffected > 0;
    }

    public boolean hasActiveSchedules(Long jobId) {
        String sql = "SELECT COUNT(*) FROM job_schedules WHERE job_id = ? AND is_active = true";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, jobId);
        return count > 0;
    }

    public List<Long> getScheduleIdsForJob(Long jobId) {
        String sql = "SELECT schedule_id FROM job_schedules WHERE job_id = ? AND is_active = true";
        return jdbcTemplate.queryForList(sql, Long.class, jobId);
    }

    public List<Long> getJobIdsForSchedule(Long scheduleId) {
        String sql = "SELECT job_id FROM job_schedules WHERE schedule_id = ? AND is_active = true";
        return jdbcTemplate.queryForList(sql, Long.class, scheduleId);
    }
} 
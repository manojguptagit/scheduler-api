package com.home.schedule.api.dao;

import com.home.schedule.api.vo.ScheduleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ScheduleDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<ScheduleVO> scheduleRowMapper = new RowMapper<ScheduleVO>() {
        @Override
        public ScheduleVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return ScheduleVO.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .cronExpression(rs.getString("cron_expression"))
                    .timezone(rs.getString("timezone"))
                    .isActive(rs.getBoolean("is_active"))
                    .createdBy(rs.getLong("created_by"))
                    .createdAt(rs.getObject("created_at", LocalDateTime.class))
                    .updatedAt(rs.getObject("updated_at", LocalDateTime.class))
                    .build();
        }
    };

    public List<ScheduleVO> findAll() {
        String sql = "SELECT * FROM schedules ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, scheduleRowMapper);
    }

    public List<ScheduleVO> findActiveSchedules() {
        String sql = "SELECT * FROM schedules WHERE is_active = true ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, scheduleRowMapper);
    }

    public Optional<ScheduleVO> findById(Long id) {
        String sql = "SELECT * FROM schedules WHERE id = ?";
        List<ScheduleVO> schedules = jdbcTemplate.query(sql, scheduleRowMapper, id);
        return schedules.isEmpty() ? Optional.empty() : Optional.of(schedules.get(0));
    }

    public List<ScheduleVO> findByName(String name) {
        String sql = "SELECT * FROM schedules WHERE name LIKE ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, scheduleRowMapper, "%" + name + "%");
    }

    public List<ScheduleVO> findByCronExpression(String cronExpression) {
        String sql = "SELECT * FROM schedules WHERE cron_expression = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, scheduleRowMapper, cronExpression);
    }

    public List<ScheduleVO> findByCreatedBy(Long createdBy) {
        String sql = "SELECT * FROM schedules WHERE created_by = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, scheduleRowMapper, createdBy);
    }

    public List<ScheduleVO> findByStatus(Boolean isActive) {
        String sql = "SELECT * FROM schedules WHERE is_active = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, scheduleRowMapper, isActive);
    }

    public ScheduleVO save(ScheduleVO schedule) {
        if (schedule.getId() == null) {
            return insert(schedule);
        } else {
            return update(schedule);
        }
    }

    private ScheduleVO insert(ScheduleVO schedule) {
        String sql = "INSERT INTO schedules (name, description, cron_expression, timezone, is_active, created_by) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        jdbcTemplate.update(sql,
                schedule.getName(),
                schedule.getDescription(),
                schedule.getCronExpression(),
                schedule.getTimezone() != null ? schedule.getTimezone() : "UTC",
                schedule.getIsActive() != null ? schedule.getIsActive() : true,
                schedule.getCreatedBy()
        );

        return findByName(schedule.getName()).stream().findFirst().orElse(null);
    }

    private ScheduleVO update(ScheduleVO schedule) {
        String sql = "UPDATE schedules SET name = ?, description = ?, cron_expression = ?, " +
                    "timezone = ?, is_active = ?, updated_at = CURRENT_TIMESTAMP " +
                    "WHERE id = ?";
        
        jdbcTemplate.update(sql,
                schedule.getName(),
                schedule.getDescription(),
                schedule.getCronExpression(),
                schedule.getTimezone(),
                schedule.getIsActive(),
                schedule.getId()
        );

        return findById(schedule.getId()).orElse(null);
    }

    public boolean deleteById(Long id) {
        String sql = "DELETE FROM schedules WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return rowsAffected > 0;
    }

    public boolean deactivateSchedule(Long id) {
        String sql = "UPDATE schedules SET is_active = false, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return rowsAffected > 0;
    }

    public boolean activateSchedule(Long id) {
        String sql = "UPDATE schedules SET is_active = true, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return rowsAffected > 0;
    }

    public boolean existsByName(String name) {
        String sql = "SELECT COUNT(*) FROM schedules WHERE name = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, name);
        return count > 0;
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM schedules";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public long countActiveSchedules() {
        String sql = "SELECT COUNT(*) FROM schedules WHERE is_active = true";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public List<ScheduleVO> findSchedulesCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT * FROM schedules WHERE created_at BETWEEN ? AND ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, scheduleRowMapper, startDate, endDate);
    }

    public List<ScheduleVO> findByDescription(String description) {
        String sql = "SELECT * FROM schedules WHERE description LIKE ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, scheduleRowMapper, "%" + description + "%");
    }

    public List<ScheduleVO> findByTimezone(String timezone) {
        String sql = "SELECT * FROM schedules WHERE timezone = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, scheduleRowMapper, timezone);
    }

    public List<ScheduleVO> findSchedulesWithJobs() {
        String sql = "SELECT DISTINCT s.* FROM schedules s " +
                    "INNER JOIN job_schedules js ON s.id = js.schedule_id " +
                    "WHERE js.is_active = true " +
                    "ORDER BY s.created_at DESC";
        return jdbcTemplate.query(sql, scheduleRowMapper);
    }

    public List<ScheduleVO> findSchedulesWithoutJobs() {
        String sql = "SELECT s.* FROM schedules s " +
                    "LEFT JOIN job_schedules js ON s.id = js.schedule_id " +
                    "WHERE js.schedule_id IS NULL " +
                    "ORDER BY s.created_at DESC";
        return jdbcTemplate.query(sql, scheduleRowMapper);
    }

    public List<ScheduleVO> findByJobId(Long jobId) {
        String sql = "SELECT s.* FROM schedules s " +
                    "INNER JOIN job_schedules js ON s.id = js.schedule_id " +
                    "WHERE js.job_id = ? AND js.is_active = true " +
                    "ORDER BY s.created_at DESC";
        return jdbcTemplate.query(sql, scheduleRowMapper, jobId);
    }

    public List<ScheduleVO> findSchedulesWithRecentExecutions(int days) {
        String sql = "SELECT DISTINCT s.* FROM schedules s " +
                    "INNER JOIN job_executions je ON s.id = je.schedule_id " +
                    "WHERE je.created_at >= DATE_SUB(CURRENT_DATE, INTERVAL ? DAY) " +
                    "ORDER BY s.created_at DESC";
        return jdbcTemplate.query(sql, scheduleRowMapper, days);
    }

    public List<ScheduleVO> findSchedulesWithFailedExecutions(int days) {
        String sql = "SELECT DISTINCT s.* FROM schedules s " +
                    "INNER JOIN job_executions je ON s.id = je.schedule_id " +
                    "WHERE je.status = 'FAILED' AND je.created_at >= DATE_SUB(CURRENT_DATE, INTERVAL ? DAY) " +
                    "ORDER BY s.created_at DESC";
        return jdbcTemplate.query(sql, scheduleRowMapper, days);
    }

    public List<ScheduleVO> findSchedulesByExecutionStatus(String status) {
        String sql = "SELECT DISTINCT s.* FROM schedules s " +
                    "INNER JOIN job_executions je ON s.id = je.schedule_id " +
                    "WHERE je.status = ? " +
                    "ORDER BY s.created_at DESC";
        return jdbcTemplate.query(sql, scheduleRowMapper, status);
    }

    public boolean validateCronExpression(String cronExpression) {
        // Basic cron expression validation
        if (cronExpression == null || cronExpression.trim().isEmpty()) {
            return false;
        }
        
        String[] parts = cronExpression.split("\\s+");
        return parts.length >= 6; // Standard cron has 6-7 parts
    }
} 
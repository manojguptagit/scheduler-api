package com.home.schedule.api.dao;

import com.home.schedule.api.vo.JobVO;
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
public class JobDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<JobVO> jobRowMapper = new RowMapper<JobVO>() {
        @Override
        public JobVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return JobVO.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .jobClass(rs.getString("job_class"))
                    .parameters(rs.getString("parameters"))
                    .isActive(rs.getBoolean("is_active"))
                    .createdBy(rs.getLong("created_by"))
                    .createdAt(rs.getObject("created_at", LocalDateTime.class))
                    .updatedAt(rs.getObject("updated_at", LocalDateTime.class))
                    .build();
        }
    };

    public List<JobVO> findAll() {
        String sql = "SELECT * FROM jobs ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, jobRowMapper);
    }

    public List<JobVO> findActiveJobs() {
        String sql = "SELECT * FROM jobs WHERE is_active = true ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, jobRowMapper);
    }

    public Optional<JobVO> findById(Long id) {
        String sql = "SELECT * FROM jobs WHERE id = ?";
        List<JobVO> jobs = jdbcTemplate.query(sql, jobRowMapper, id);
        return jobs.isEmpty() ? Optional.empty() : Optional.of(jobs.get(0));
    }

    public List<JobVO> findByName(String name) {
        String sql = "SELECT * FROM jobs WHERE name LIKE ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, jobRowMapper, "%" + name + "%");
    }

    public List<JobVO> findByJobClass(String jobClass) {
        String sql = "SELECT * FROM jobs WHERE job_class = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, jobRowMapper, jobClass);
    }

    public List<JobVO> findByCreatedBy(Long createdBy) {
        String sql = "SELECT * FROM jobs WHERE created_by = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, jobRowMapper, createdBy);
    }

    public List<JobVO> findByStatus(Boolean isActive) {
        String sql = "SELECT * FROM jobs WHERE is_active = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, jobRowMapper, isActive);
    }

    public JobVO save(JobVO job) {
        if (job.getId() == null) {
            return insert(job);
        } else {
            return update(job);
        }
    }

    private JobVO insert(JobVO job) {
        String sql = "INSERT INTO jobs (name, description, job_class, parameters, is_active, created_by) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        jdbcTemplate.update(sql,
                job.getName(),
                job.getDescription(),
                job.getJobClass(),
                job.getParameters(),
                job.getIsActive() != null ? job.getIsActive() : true,
                job.getCreatedBy()
        );

        return findByName(job.getName()).stream().findFirst().orElse(null);
    }

    private JobVO update(JobVO job) {
        String sql = "UPDATE jobs SET name = ?, description = ?, job_class = ?, " +
                    "parameters = ?, is_active = ?, updated_at = CURRENT_TIMESTAMP " +
                    "WHERE id = ?";
        
        jdbcTemplate.update(sql,
                job.getName(),
                job.getDescription(),
                job.getJobClass(),
                job.getParameters(),
                job.getIsActive(),
                job.getId()
        );

        return findById(job.getId()).orElse(null);
    }

    public boolean deleteById(Long id) {
        String sql = "DELETE FROM jobs WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return rowsAffected > 0;
    }

    public boolean deactivateJob(Long id) {
        String sql = "UPDATE jobs SET is_active = false, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return rowsAffected > 0;
    }

    public boolean activateJob(Long id) {
        String sql = "UPDATE jobs SET is_active = true, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return rowsAffected > 0;
    }

    public boolean updateParameters(Long id, String parameters) {
        String sql = "UPDATE jobs SET parameters = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, parameters, id);
        return rowsAffected > 0;
    }

    public boolean existsByName(String name) {
        String sql = "SELECT COUNT(*) FROM jobs WHERE name = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, name);
        return count > 0;
    }

    public boolean existsByJobClass(String jobClass) {
        String sql = "SELECT COUNT(*) FROM jobs WHERE job_class = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, jobClass);
        return count > 0;
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM jobs";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public long countActiveJobs() {
        String sql = "SELECT COUNT(*) FROM jobs WHERE is_active = true";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public List<JobVO> findJobsCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT * FROM jobs WHERE created_at BETWEEN ? AND ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, jobRowMapper, startDate, endDate);
    }

    public List<JobVO> findJobsByDescription(String description) {
        String sql = "SELECT * FROM jobs WHERE description LIKE ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, jobRowMapper, "%" + description + "%");
    }

    public List<JobVO> findJobsWithSchedules() {
        String sql = "SELECT DISTINCT j.* FROM jobs j " +
                    "INNER JOIN job_schedules js ON j.id = js.job_id " +
                    "WHERE js.is_active = true " +
                    "ORDER BY j.created_at DESC";
        return jdbcTemplate.query(sql, jobRowMapper);
    }

    public List<JobVO> findJobsWithoutSchedules() {
        String sql = "SELECT j.* FROM jobs j " +
                    "LEFT JOIN job_schedules js ON j.id = js.job_id " +
                    "WHERE js.job_id IS NULL " +
                    "ORDER BY j.created_at DESC";
        return jdbcTemplate.query(sql, jobRowMapper);
    }

    public List<JobVO> findJobsByDependencyType(String dependencyType) {
        String sql = "SELECT DISTINCT j.* FROM jobs j " +
                    "INNER JOIN job_dependencies jd ON j.id = jd.dependent_job_id " +
                    "WHERE jd.dependency_type = ? " +
                    "ORDER BY j.created_at DESC";
        return jdbcTemplate.query(sql, jobRowMapper, dependencyType);
    }

    public List<JobVO> findJobsWithNotifications() {
        String sql = "SELECT DISTINCT j.* FROM jobs j " +
                    "INNER JOIN job_notifications jn ON j.id = jn.job_id " +
                    "WHERE jn.is_active = true " +
                    "ORDER BY j.created_at DESC";
        return jdbcTemplate.query(sql, jobRowMapper);
    }

    public List<JobVO> findJobsByNotificationType(String notificationType) {
        String sql = "SELECT DISTINCT j.* FROM jobs j " +
                    "INNER JOIN job_notifications jn ON j.id = jn.job_id " +
                    "WHERE jn.notification_type = ? AND jn.is_active = true " +
                    "ORDER BY j.created_at DESC";
        return jdbcTemplate.query(sql, jobRowMapper, notificationType);
    }

    public List<JobVO> findJobsWithRecentExecutions(int days) {
        String sql = "SELECT DISTINCT j.* FROM jobs j " +
                    "INNER JOIN job_executions je ON j.id = je.job_id " +
                    "WHERE je.created_at >= DATE_SUB(CURRENT_DATE, INTERVAL ? DAY) " +
                    "ORDER BY j.created_at DESC";
        return jdbcTemplate.query(sql, jobRowMapper, days);
    }

    public List<JobVO> findJobsWithFailedExecutions(int days) {
        String sql = "SELECT DISTINCT j.* FROM jobs j " +
                    "INNER JOIN job_executions je ON j.id = je.job_id " +
                    "WHERE je.status = 'FAILED' AND je.created_at >= DATE_SUB(CURRENT_DATE, INTERVAL ? DAY) " +
                    "ORDER BY j.created_at DESC";
        return jdbcTemplate.query(sql, jobRowMapper, days);
    }

    public List<JobVO> findJobsByExecutionStatus(String status) {
        String sql = "SELECT DISTINCT j.* FROM jobs j " +
                    "INNER JOIN job_executions je ON j.id = je.job_id " +
                    "WHERE je.status = ? " +
                    "ORDER BY j.created_at DESC";
        return jdbcTemplate.query(sql, jobRowMapper, status);
    }

    public List<JobVO> findJobsByAverageExecutionTime(long minDurationMs, long maxDurationMs) {
        String sql = "SELECT DISTINCT j.* FROM jobs j " +
                    "INNER JOIN job_statistics js ON j.id = js.job_id " +
                    "WHERE js.avg_duration_ms BETWEEN ? AND ? " +
                    "ORDER BY j.created_at DESC";
        return jdbcTemplate.query(sql, jobRowMapper, minDurationMs, maxDurationMs);
    }

    public List<JobVO> findJobsBySuccessRate(double minSuccessRate) {
        String sql = "SELECT DISTINCT j.* FROM jobs j " +
                    "INNER JOIN job_statistics js ON j.id = js.job_id " +
                    "WHERE (js.successful_executions * 100.0 / js.total_executions) >= ? " +
                    "ORDER BY j.created_at DESC";
        return jdbcTemplate.query(sql, jobRowMapper, minSuccessRate);
    }
} 
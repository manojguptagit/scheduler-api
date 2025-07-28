package com.home.schedule.api.dao;

import com.home.schedule.api.vo.JobExecutionVO;
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
public class JobExecutionDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<JobExecutionVO> executionRowMapper = new RowMapper<JobExecutionVO>() {
        @Override
        public JobExecutionVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return JobExecutionVO.builder()
                    .id(rs.getLong("id"))
                    .jobId(rs.getLong("job_id"))
                    .scheduleId(rs.getObject("schedule_id", Long.class))
                    .executionId(rs.getString("execution_id"))
                    .status(rs.getString("status"))
                    .startTime(rs.getObject("start_time", LocalDateTime.class))
                    .endTime(rs.getObject("end_time", LocalDateTime.class))
                    .durationMs(rs.getObject("duration_ms", Long.class))
                    .resultMessage(rs.getString("result_message"))
                    .errorMessage(rs.getString("error_message"))
                    .stackTrace(rs.getString("stack_trace"))
                    .parameters(rs.getString("parameters"))
                    .createdAt(rs.getObject("created_at", LocalDateTime.class))
                    .build();
        }
    };

    public List<JobExecutionVO> findAll() {
        String sql = "SELECT * FROM job_executions ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, executionRowMapper);
    }

    public Optional<JobExecutionVO> findById(Long id) {
        String sql = "SELECT * FROM job_executions WHERE id = ?";
        List<JobExecutionVO> executions = jdbcTemplate.query(sql, executionRowMapper, id);
        return executions.isEmpty() ? Optional.empty() : Optional.of(executions.get(0));
    }

    public Optional<JobExecutionVO> findByExecutionId(String executionId) {
        String sql = "SELECT * FROM job_executions WHERE execution_id = ?";
        List<JobExecutionVO> executions = jdbcTemplate.query(sql, executionRowMapper, executionId);
        return executions.isEmpty() ? Optional.empty() : Optional.of(executions.get(0));
    }

    public List<JobExecutionVO> findByJobId(Long jobId) {
        String sql = "SELECT * FROM job_executions WHERE job_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, executionRowMapper, jobId);
    }

    public List<JobExecutionVO> findByScheduleId(Long scheduleId) {
        String sql = "SELECT * FROM job_executions WHERE schedule_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, executionRowMapper, scheduleId);
    }

    public List<JobExecutionVO> findByStatus(String status) {
        String sql = "SELECT * FROM job_executions WHERE status = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, executionRowMapper, status);
    }

    public List<JobExecutionVO> findByJobIdAndStatus(Long jobId, String status) {
        String sql = "SELECT * FROM job_executions WHERE job_id = ? AND status = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, executionRowMapper, jobId, status);
    }

    public List<JobExecutionVO> findRunningExecutions() {
        String sql = "SELECT * FROM job_executions WHERE status = 'RUNNING' ORDER BY start_time DESC";
        return jdbcTemplate.query(sql, executionRowMapper);
    }

    public List<JobExecutionVO> findRunningExecutionsByJobId(Long jobId) {
        String sql = "SELECT * FROM job_executions WHERE job_id = ? AND status = 'RUNNING' ORDER BY start_time DESC";
        return jdbcTemplate.query(sql, executionRowMapper, jobId);
    }

    public List<JobExecutionVO> findPendingExecutions() {
        String sql = "SELECT * FROM job_executions WHERE status = 'PENDING' ORDER BY created_at ASC";
        return jdbcTemplate.query(sql, executionRowMapper);
    }

    public List<JobExecutionVO> findFailedExecutions() {
        String sql = "SELECT * FROM job_executions WHERE status = 'FAILED' ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, executionRowMapper);
    }

    public List<JobExecutionVO> findCompletedExecutions() {
        String sql = "SELECT * FROM job_executions WHERE status = 'COMPLETED' ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, executionRowMapper);
    }

    public List<JobExecutionVO> findCancelledExecutions() {
        String sql = "SELECT * FROM job_executions WHERE status = 'CANCELLED' ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, executionRowMapper);
    }

    public JobExecutionVO save(JobExecutionVO execution) {
        if (execution.getId() == null) {
            return insert(execution);
        } else {
            return update(execution);
        }
    }

    private JobExecutionVO insert(JobExecutionVO execution) {
        String sql = "INSERT INTO job_executions (job_id, schedule_id, execution_id, status, start_time, " +
                    "end_time, duration_ms, result_message, error_message, stack_trace, parameters) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        jdbcTemplate.update(sql,
                execution.getJobId(),
                execution.getScheduleId(),
                execution.getExecutionId(),
                execution.getStatus(),
                execution.getStartTime(),
                execution.getEndTime(),
                execution.getDurationMs(),
                execution.getResultMessage(),
                execution.getErrorMessage(),
                execution.getStackTrace(),
                execution.getParameters()
        );

        return findByExecutionId(execution.getExecutionId()).orElse(null);
    }

    private JobExecutionVO update(JobExecutionVO execution) {
        String sql = "UPDATE job_executions SET job_id = ?, schedule_id = ?, execution_id = ?, " +
                    "status = ?, start_time = ?, end_time = ?, duration_ms = ?, result_message = ?, " +
                    "error_message = ?, stack_trace = ?, parameters = ? WHERE id = ?";
        
        jdbcTemplate.update(sql,
                execution.getJobId(),
                execution.getScheduleId(),
                execution.getExecutionId(),
                execution.getStatus(),
                execution.getStartTime(),
                execution.getEndTime(),
                execution.getDurationMs(),
                execution.getResultMessage(),
                execution.getErrorMessage(),
                execution.getStackTrace(),
                execution.getParameters(),
                execution.getId()
        );

        return findById(execution.getId()).orElse(null);
    }

    public boolean updateStatus(Long id, String status) {
        String sql = "UPDATE job_executions SET status = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, status, id);
        return rowsAffected > 0;
    }

    public boolean updateStatus(String executionId, String status) {
        String sql = "UPDATE job_executions SET status = ? WHERE execution_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, status, executionId);
        return rowsAffected > 0;
    }

    public boolean startExecution(Long id) {
        String sql = "UPDATE job_executions SET status = 'RUNNING', start_time = CURRENT_TIMESTAMP WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return rowsAffected > 0;
    }

    public boolean completeExecution(Long id, String resultMessage, Long durationMs) {
        String sql = "UPDATE job_executions SET status = 'COMPLETED', end_time = CURRENT_TIMESTAMP, " +
                    "duration_ms = ?, result_message = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, durationMs, resultMessage, id);
        return rowsAffected > 0;
    }

    public boolean failExecution(Long id, String errorMessage, String stackTrace, Long durationMs) {
        String sql = "UPDATE job_executions SET status = 'FAILED', end_time = CURRENT_TIMESTAMP, " +
                    "duration_ms = ?, error_message = ?, stack_trace = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, durationMs, errorMessage, stackTrace, id);
        return rowsAffected > 0;
    }

    public boolean cancelExecution(Long id) {
        String sql = "UPDATE job_executions SET status = 'CANCELLED', end_time = CURRENT_TIMESTAMP WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return rowsAffected > 0;
    }

    public boolean deleteById(Long id) {
        String sql = "DELETE FROM job_executions WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return rowsAffected > 0;
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM job_executions";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public long countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM job_executions WHERE status = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, status);
    }

    public long countByJobId(Long jobId) {
        String sql = "SELECT COUNT(*) FROM job_executions WHERE job_id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, jobId);
    }

    public List<JobExecutionVO> findExecutionsCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT * FROM job_executions WHERE created_at BETWEEN ? AND ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, executionRowMapper, startDate, endDate);
    }

    public List<JobExecutionVO> findExecutionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT * FROM job_executions WHERE start_time BETWEEN ? AND ? ORDER BY start_time DESC";
        return jdbcTemplate.query(sql, executionRowMapper, startDate, endDate);
    }

    public List<JobExecutionVO> findRecentExecutions(int limit) {
        String sql = "SELECT * FROM job_executions ORDER BY created_at DESC LIMIT ?";
        return jdbcTemplate.query(sql, executionRowMapper, limit);
    }

    public List<JobExecutionVO> findRecentExecutionsByJobId(Long jobId, int limit) {
        String sql = "SELECT * FROM job_executions WHERE job_id = ? ORDER BY created_at DESC LIMIT ?";
        return jdbcTemplate.query(sql, executionRowMapper, jobId, limit);
    }

    public List<JobExecutionVO> findRecentSuccessfulExecutions(Long jobId, int days) {
        String sql = "SELECT * FROM job_executions WHERE job_id = ? AND status = 'COMPLETED' " +
                    "AND created_at >= DATE_SUB(CURRENT_DATE, INTERVAL ? DAY) ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, executionRowMapper, jobId, days);
    }

    public List<JobExecutionVO> findRecentFailedExecutions(Long jobId, int days) {
        String sql = "SELECT * FROM job_executions WHERE job_id = ? AND status = 'FAILED' " +
                    "AND created_at >= DATE_SUB(CURRENT_DATE, INTERVAL ? DAY) ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, executionRowMapper, jobId, days);
    }

    public List<JobExecutionVO> findLongRunningExecutions(long minDurationMs) {
        String sql = "SELECT * FROM job_executions WHERE duration_ms > ? ORDER BY duration_ms DESC";
        return jdbcTemplate.query(sql, executionRowMapper, minDurationMs);
    }

    public List<JobExecutionVO> findExecutionsByDurationRange(long minDurationMs, long maxDurationMs) {
        String sql = "SELECT * FROM job_executions WHERE duration_ms BETWEEN ? AND ? ORDER BY duration_ms DESC";
        return jdbcTemplate.query(sql, executionRowMapper, minDurationMs, maxDurationMs);
    }

    public List<JobExecutionVO> findExecutionsWithErrors() {
        String sql = "SELECT * FROM job_executions WHERE error_message IS NOT NULL AND error_message != '' " +
                    "ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, executionRowMapper);
    }

    public List<JobExecutionVO> findExecutionsByParameters(String parameterValue) {
        String sql = "SELECT * FROM job_executions WHERE parameters LIKE ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, executionRowMapper, "%" + parameterValue + "%");
    }

    public boolean existsByExecutionId(String executionId) {
        String sql = "SELECT COUNT(*) FROM job_executions WHERE execution_id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, executionId);
        return count > 0;
    }

    public void cleanupOldExecutions(int daysToKeep) {
        String sql = "DELETE FROM job_executions WHERE created_at < DATE_SUB(CURRENT_DATE, INTERVAL ? DAY)";
        jdbcTemplate.update(sql, daysToKeep);
    }
} 
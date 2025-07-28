package com.home.schedule.api.service;

import com.home.schedule.api.dao.ScheduleDAO;
import com.home.schedule.api.vo.ScheduleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ScheduleService {

    @Autowired
    private ScheduleDAO scheduleDAO;

    public List<ScheduleVO> getAllSchedules() {
        return scheduleDAO.findAll();
    }

    public List<ScheduleVO> getActiveSchedules() {
        return scheduleDAO.findActiveSchedules();
    }

    public Optional<ScheduleVO> getScheduleById(Long id) {
        return scheduleDAO.findById(id);
    }

    public List<ScheduleVO> getSchedulesByName(String name) {
        return scheduleDAO.findByName(name);
    }

    public List<ScheduleVO> getSchedulesByCronExpression(String cronExpression) {
        return scheduleDAO.findByCronExpression(cronExpression);
    }

    public List<ScheduleVO> getSchedulesByCreator(Long createdBy) {
        return scheduleDAO.findByCreatedBy(createdBy);
    }

    public ScheduleVO createSchedule(ScheduleVO schedule) {
        if (scheduleDAO.existsByName(schedule.getName())) {
            throw new IllegalArgumentException("Schedule with name '" + schedule.getName() + "' already exists");
        }

        if (!validateCronExpression(schedule.getCronExpression())) {
            throw new IllegalArgumentException("Invalid cron expression: " + schedule.getCronExpression());
        }

        return scheduleDAO.save(schedule);
    }

    public ScheduleVO updateSchedule(Long id, ScheduleVO schedule) {
        Optional<ScheduleVO> existingSchedule = scheduleDAO.findById(id);
        if (existingSchedule.isEmpty()) {
            throw new IllegalArgumentException("Schedule with id " + id + " not found");
        }

        if (!validateCronExpression(schedule.getCronExpression())) {
            throw new IllegalArgumentException("Invalid cron expression: " + schedule.getCronExpression());
        }

        schedule.setId(id);
        return scheduleDAO.save(schedule);
    }

    public boolean deleteSchedule(Long id) {
        return scheduleDAO.deleteById(id);
    }

    public boolean activateSchedule(Long id) {
        return scheduleDAO.activateSchedule(id);
    }

    public boolean deactivateSchedule(Long id) {
        return scheduleDAO.deactivateSchedule(id);
    }

    public boolean validateCronExpression(String cronExpression) {
        return scheduleDAO.validateCronExpression(cronExpression);
    }

    public long getTotalScheduleCount() {
        return scheduleDAO.count();
    }

    public long getActiveScheduleCount() {
        return scheduleDAO.countActiveSchedules();
    }

    public List<ScheduleVO> getSchedulesWithJobs() {
        return scheduleDAO.findSchedulesWithJobs();
    }

    public List<ScheduleVO> getSchedulesWithoutJobs() {
        return scheduleDAO.findSchedulesWithoutJobs();
    }

    public List<ScheduleVO> getSchedulesByJobId(Long jobId) {
        return scheduleDAO.findByJobId(jobId);
    }

    public List<ScheduleVO> getSchedulesWithRecentExecutions(int days) {
        return scheduleDAO.findSchedulesWithRecentExecutions(days);
    }

    public List<ScheduleVO> getSchedulesWithFailedExecutions(int days) {
        return scheduleDAO.findSchedulesWithFailedExecutions(days);
    }

    public List<ScheduleVO> getSchedulesByExecutionStatus(String status) {
        return scheduleDAO.findSchedulesByExecutionStatus(status);
    }

    public List<ScheduleVO> getSchedulesByTimezone(String timezone) {
        return scheduleDAO.findByTimezone(timezone);
    }

    public List<ScheduleVO> getSchedulesByDescription(String description) {
        return scheduleDAO.findByDescription(description);
    }

    public List<ScheduleVO> getSchedulesByStatus(Boolean isActive) {
        return scheduleDAO.findByStatus(isActive);
    }
} 
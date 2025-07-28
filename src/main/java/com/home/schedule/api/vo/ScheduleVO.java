package com.home.schedule.api.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Value Object (VO) representing a schedule definition in the scheduling system.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleVO {
    
    private Long id;
    private String name;
    private String description;
    private String cronExpression;
    private String timezone;
    private Boolean isActive;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional fields for convenience - not stored in database
    private List<JobVO> jobs;
    private String nextExecutionTime;
    private String previousExecutionTime;
} 
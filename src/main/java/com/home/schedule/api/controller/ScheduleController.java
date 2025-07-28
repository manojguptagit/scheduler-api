package com.home.schedule.api.controller;

import com.home.schedule.api.service.ScheduleService;
import com.home.schedule.api.vo.ScheduleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/schedules")
@CrossOrigin(origins = "*")
@Tag(name = "Schedule Management", description = "APIs for managing job schedules and cron expressions")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Operation(
        summary = "Get all schedules",
        description = "Retrieve a list of all schedules in the system, ordered by creation date (newest first)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved schedules",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ScheduleVO.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<ScheduleVO>> getAllSchedules() {
        try {
            List<ScheduleVO> schedules = scheduleService.getAllSchedules();
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/active")
    public ResponseEntity<List<ScheduleVO>> getActiveSchedules() {
        try {
            List<ScheduleVO> schedules = scheduleService.getActiveSchedules();
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleVO> getScheduleById(@PathVariable Long id) {
        try {
            Optional<ScheduleVO> schedule = scheduleService.getScheduleById(id);
            return schedule.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
        summary = "Create a new schedule",
        description = "Create a new schedule with the provided data. Cron expressions will be validated."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Schedule created successfully",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ScheduleVO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid schedule data or invalid cron expression"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<ScheduleVO> createSchedule(
        @Parameter(description = "Schedule data to create", required = true,
            content = @Content(examples = {
                @ExampleObject(name = "Daily Schedule", value = "{\n" +
                    "    \"name\": \"Daily Backup\",\n" +
                    "    \"description\": \"Daily backup at midnight\",\n" +
                    "    \"cronExpression\": \"0 0 * * *\",\n" +
                    "    \"timezone\": \"UTC\",\n" +
                    "    \"isActive\": true,\n" +
                    "    \"createdBy\": 1\n" +
                    "}"),
                @ExampleObject(name = "Weekly Schedule", value = "{\n" +
                    "    \"name\": \"Weekly Report\",\n" +
                    "    \"description\": \"Weekly report every Monday at 9 AM\",\n" +
                    "    \"cronExpression\": \"0 9 * * 1\",\n" +
                    "    \"timezone\": \"America/New_York\",\n" +
                    "    \"isActive\": true,\n" +
                    "    \"createdBy\": 1\n" +
                    "}")
            }))
        @RequestBody ScheduleVO schedule) {
        try {
            ScheduleVO createdSchedule = scheduleService.createSchedule(schedule);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSchedule);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScheduleVO> updateSchedule(@PathVariable Long id, @RequestBody ScheduleVO schedule) {
        try {
            ScheduleVO updatedSchedule = scheduleService.updateSchedule(id, schedule);
            return ResponseEntity.ok(updatedSchedule);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        try {
            boolean deleted = scheduleService.deleteSchedule(id);
            return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateSchedule(@PathVariable Long id) {
        try {
            boolean activated = scheduleService.activateSchedule(id);
            return activated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateSchedule(@PathVariable Long id) {
        try {
            boolean deactivated = scheduleService.deactivateSchedule(id);
            return deactivated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
        summary = "Validate cron expression",
        description = "Validate a cron expression to ensure it is syntactically correct"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cron expression validation result",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(type = "boolean"))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/validate-cron")
    public ResponseEntity<Boolean> validateCronExpression(
        @Parameter(description = "Cron expression to validate", required = true,
            content = @Content(examples = {
                @ExampleObject(name = "Daily at midnight", value = "0 0 * * *"),
                @ExampleObject(name = "Every hour", value = "0 * * * *"),
                @ExampleObject(name = "Weekly on Monday", value = "0 9 * * 1"),
                @ExampleObject(name = "Monthly on 1st", value = "0 0 1 * *")
            }))
        @RequestParam String cronExpression) {
        try {
            boolean isValid = scheduleService.validateCronExpression(cronExpression);
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/stats/count")
    public ResponseEntity<Long> getTotalScheduleCount() {
        try {
            long count = scheduleService.getTotalScheduleCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/stats/active-count")
    public ResponseEntity<Long> getActiveScheduleCount() {
        try {
            long count = scheduleService.getActiveScheduleCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 
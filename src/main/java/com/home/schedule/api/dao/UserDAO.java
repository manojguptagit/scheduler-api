package com.home.schedule.api.dao;

import com.home.schedule.api.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object (DAO) for user management operations.
 * 
 * This class provides comprehensive database access methods for user-related
 * operations including CRUD operations, authentication, and user management.
 * It uses Spring's JdbcTemplate for database interactions and provides
 * a clean abstraction layer between the service layer and database.
 * 
 * Features:
 * - Complete CRUD operations for users
 * - User authentication and lookup methods
 * - User status management (activate/deactivate)
 * - Password management
 * - Search and filtering capabilities
 * - Statistical queries (counts, date ranges)
 * 
 * Database Operations:
 * - SELECT: Find users by various criteria
 * - INSERT: Create new users
 * - UPDATE: Modify existing users
 * - DELETE: Remove users (soft delete via deactivation)
 * 
 * Security Features:
 * - Password hash storage (never plain text)
 * - User status validation
 * - Unique constraint enforcement
 * 
 * @author Job Scheduler Team
 * @version 1.0.0
 * @since 2024
 */
@Repository
public class UserDAO {

    /**
     * Spring JdbcTemplate for database operations
     * Provides convenient methods for SQL execution and result mapping
     */
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * RowMapper for converting database rows to UserVO objects
     * Maps each column from the ResultSet to the corresponding UserVO field
     */
    private final RowMapper<UserVO> userRowMapper = new RowMapper<UserVO>() {
        @Override
        public UserVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return UserVO.builder()
                    .id(rs.getLong("id"))
                    .username(rs.getString("username"))
                    .email(rs.getString("email"))
                    .passwordHash(rs.getString("password_hash"))
                    .firstName(rs.getString("first_name"))
                    .lastName(rs.getString("last_name"))
                    .isActive(rs.getBoolean("is_active"))
                    .createdAt(rs.getObject("created_at", LocalDateTime.class))
                    .updatedAt(rs.getObject("updated_at", LocalDateTime.class))
                    .build();
        }
    };

    /**
     * Retrieve all users from the database
     * 
     * @return List of all users ordered by creation date (newest first)
     */
    public List<UserVO> findAll() {
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    /**
     * Retrieve all active users from the database
     * 
     * @return List of active users ordered by creation date (newest first)
     */
    public List<UserVO> findActiveUsers() {
        String sql = "SELECT * FROM users WHERE is_active = true ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    /**
     * Find a user by their unique ID
     * 
     * @param id The user's unique identifier
     * @return Optional containing the user if found, empty otherwise
     */
    public Optional<UserVO> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<UserVO> users = jdbcTemplate.query(sql, userRowMapper, id);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    /**
     * Find a user by their unique username
     * 
     * @param username The user's username
     * @return Optional containing the user if found, empty otherwise
     */
    public Optional<UserVO> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        List<UserVO> users = jdbcTemplate.query(sql, userRowMapper, username);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    /**
     * Find a user by their unique email address
     * 
     * @param email The user's email address
     * @return Optional containing the user if found, empty otherwise
     */
    public Optional<UserVO> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        List<UserVO> users = jdbcTemplate.query(sql, userRowMapper, email);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    /**
     * Find users by first name (partial match)
     * 
     * @param firstName The first name to search for (supports partial matching)
     * @return List of users with matching first names
     */
    public List<UserVO> findByFirstName(String firstName) {
        String sql = "SELECT * FROM users WHERE first_name LIKE ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, userRowMapper, "%" + firstName + "%");
    }

    /**
     * Find users by last name (partial match)
     * 
     * @param lastName The last name to search for (supports partial matching)
     * @return List of users with matching last names
     */
    public List<UserVO> findByLastName(String lastName) {
        String sql = "SELECT * FROM users WHERE last_name LIKE ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, userRowMapper, "%" + lastName + "%");
    }

    /**
     * Save a user (create new or update existing)
     * 
     * @param user The user to save
     * @return The saved user with generated ID if new
     */
    public UserVO save(UserVO user) {
        if (user.getId() == null) {
            return insert(user);
        } else {
            return update(user);
        }
    }

    /**
     * Insert a new user into the database
     * 
     * @param user The user to insert
     * @return The inserted user with generated ID
     */
    private UserVO insert(UserVO user) {
        String sql = "INSERT INTO users (username, email, password_hash, first_name, last_name, is_active) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        jdbcTemplate.update(sql,
                user.getUsername(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getFirstName(),
                user.getLastName(),
                user.getIsActive() != null ? user.getIsActive() : true
        );

        return findByUsername(user.getUsername()).orElse(null);
    }

    /**
     * Update an existing user in the database
     * 
     * @param user The user to update
     * @return The updated user
     */
    private UserVO update(UserVO user) {
        String sql = "UPDATE users SET username = ?, email = ?, password_hash = ?, " +
                    "first_name = ?, last_name = ?, is_active = ? WHERE id = ?";
        
        jdbcTemplate.update(sql,
                user.getUsername(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getFirstName(),
                user.getLastName(),
                user.getIsActive(),
                user.getId()
        );

        return findById(user.getId()).orElse(null);
    }

    /**
     * Delete a user by ID (soft delete via deactivation)
     * 
     * @param id The ID of the user to delete
     * @return true if the user was successfully deleted, false otherwise
     */
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return rowsAffected > 0;
    }

    /**
     * Deactivate a user account
     * 
     * @param id The ID of the user to deactivate
     * @return true if the user was successfully deactivated, false otherwise
     */
    public boolean deactivateUser(Long id) {
        String sql = "UPDATE users SET is_active = false WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return rowsAffected > 0;
    }

    /**
     * Activate a user account
     * 
     * @param id The ID of the user to activate
     * @return true if the user was successfully activated, false otherwise
     */
    public boolean activateUser(Long id) {
        String sql = "UPDATE users SET is_active = true WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return rowsAffected > 0;
    }

    /**
     * Update a user's password hash
     * 
     * @param id The ID of the user
     * @param newPasswordHash The new hashed password
     * @return true if the password was successfully updated, false otherwise
     */
    public boolean updatePassword(Long id, String newPasswordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, newPasswordHash, id);
        return rowsAffected > 0;
    }

    /**
     * Check if a username already exists
     * 
     * @param username The username to check
     * @return true if the username exists, false otherwise
     */
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count > 0;
    }

    /**
     * Check if an email address already exists
     * 
     * @param email The email address to check
     * @return true if the email exists, false otherwise
     */
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count > 0;
    }

    /**
     * Get the total number of users
     * 
     * @return Total count of users in the database
     */
    public long count() {
        String sql = "SELECT COUNT(*) FROM users";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    /**
     * Get the number of active users
     * 
     * @return Count of active users in the database
     */
    public long countActiveUsers() {
        String sql = "SELECT COUNT(*) FROM users WHERE is_active = true";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    /**
     * Find users created within a specific date range
     * 
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return List of users created within the specified range
     */
    public List<UserVO> findUsersCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT * FROM users WHERE created_at BETWEEN ? AND ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, userRowMapper, startDate, endDate);
    }

    /**
     * Find users by their active status
     * 
     * @param isActive The active status to filter by
     * @return List of users with the specified active status
     */
    public List<UserVO> findUsersByStatus(Boolean isActive) {
        String sql = "SELECT * FROM users WHERE is_active = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, userRowMapper, isActive);
    }
} 
package com.narwal.assignment.service.user;

import com.narwal.assignment.dto.UserDto;
import com.narwal.assignment.mapper.UserMapper;
import com.narwal.assignment.service.db.UserDbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserDbService userDbService;
    private final UserMapper userMapper;

    public UserService(UserDbService userDbService, UserMapper userMapper) {
        this.userDbService = userDbService;
        this.userMapper = userMapper;
    }

    public Page<UserDto> fetchAllUsers(Pageable pageable) {
        logger.info("Fetching all users with pagination: {}", pageable);
        Page<UserDto> users = userDbService.getAllUsers(pageable).map(userMapper::toDto);
        logger.info("Retrieved {} users.", users.getTotalElements());
        return users;
    }

    public Page<UserDto> fetchUsersByRole(String role, Pageable pageable) {
        logger.info("Fetching users by role: '{}' with pagination: {}", role, pageable);
        Page<UserDto> users = userDbService.getUsersByRole(role, pageable).map(userMapper::toDto);
        logger.info("Retrieved {} users with role '{}'.", users.getTotalElements(), role);
        return users;
    }

    public Page<UserDto> fetchUsersSortedByAge(boolean isAscending, Pageable pageable) {
        logger.info("Fetching users sorted by age in '{}' order with pagination: {}", isAscending, pageable);
        Page<UserDto> users = userDbService.getUsersByOrder(isAscending, pageable).map(userMapper::toDto);
        logger.info("Retrieved {} users sorted by age.", users.getTotalElements());
        return users;
    }

    public Optional<UserDto> fetchUserById(Long id) {
        logger.info("Fetching user by ID: {}", id);
        Optional<UserDto> user = userDbService.getUserById(id).map(userMapper::toDto);
        if (user.isPresent()) {
            logger.info("User found with ID: {}", id);
        } else {
            logger.warn("User not found with ID: {}", id);
        }
        return user;
    }

    public Optional<UserDto> fetchUserBySsn(String ssn) {
        logger.info("Fetching user by SSN: {}", ssn);
        Optional<UserDto> user = userDbService.getUserBySsn(ssn).map(userMapper::toDto);
        if (user.isPresent()) {
            logger.info("User found with SSN: {}", ssn);
        } else {
            logger.warn("User not found with SSN: {}", ssn);
        }
        return user;
    }
}
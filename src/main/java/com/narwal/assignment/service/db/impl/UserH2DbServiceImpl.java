package com.narwal.assignment.service.db.impl;

import com.narwal.assignment.entity.User;
import com.narwal.assignment.repository.h2.UserRepository;
import com.narwal.assignment.service.db.UserDbService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserH2DbServiceImpl implements UserDbService {

    private static final Logger logger = LoggerFactory.getLogger(UserH2DbServiceImpl.class);

    private final UserRepository userRepository;

    public UserH2DbServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Fetch all users with pagination
    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        logger.info("Fetching all users with pagination: {}", pageable);
        Page<User> users = userRepository.findAll(pageable);
        logger.debug("Retrieved {} users.", users.getTotalElements());
        return users;
    }

    // Fetch users by role with pagination
    @Override
    public Page<User> getUsersByRole(String role, Pageable pageable) {
        logger.info("Fetching users by role: '{}' with pagination: {}", role, pageable);
        Page<User> users = userRepository.findUsersByRole(role, pageable);
        logger.info("Retrieved {} users with role '{}'.", users.getTotalElements(), role);
        return users;
    }

    // Fetch users sorted by age (asc/desc) with pagination
    @Override
    public Page<User> getUsersByOrder(boolean isAscending, Pageable pageable) {
        Sort sort = isAscending ? Sort.by("age").ascending() : Sort.by("age").descending();
        // Ensure we apply sorting but preserve existing pageable settings
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );
        logger.info("Fetching users sorted by age in '{}' order with pagination: page={}, size={}",
                isAscending ? "ascending" : "descending", pageable.getPageNumber(), pageable.getPageSize());
        Page<User> users = userRepository.findAll(sortedPageable);
        if (users.isEmpty()) {
            logger.warn("No users found in database.");
        } else {
            logger.info("Retrieved {} users sorted by age.", users.getTotalElements());
        }
        return users;
    }

    // Fetch a user by ID
    @Override
    public Optional<User> getUserById(Long id) {
        logger.info("Fetching user by ID: {}", id);
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            logger.info("User found with ID: {}", id);
        } else {
            logger.warn("User not found with ID: {}", id);
        }
        return user;
    }

    // Fetch a user by SSN
    @Override
    public Optional<User> getUserBySsn(String ssn) {
        logger.info("Fetching user by SSN: {}", ssn);
        Optional<User> user = userRepository.findBySsn(ssn);
        if (user.isPresent()) {
            logger.info("User found with SSN: {}", ssn);
        } else {
            logger.warn("User not found with SSN: {}", ssn);
        }
        return user;
    }

    @Override
    @Transactional
    public void saveAll(List<User> users) {
        logger.info("Saving {} users to the database.", users.size());
        userRepository.saveAll(users);
        logger.info("Successfully saved {} users.", users.size());
    }
}

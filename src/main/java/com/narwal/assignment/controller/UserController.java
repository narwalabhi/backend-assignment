package com.narwal.assignment.controller;

import com.narwal.assignment.dto.ApiResponse;
import com.narwal.assignment.dto.UserDto;
import com.narwal.assignment.service.load.UserLoadingService;
import com.narwal.assignment.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UserLoadingService userLoadingService;

    public UserController(UserService userService, UserLoadingService userLoadingService) {
        this.userService = userService;
        this.userLoadingService = userLoadingService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserDto>>> getAllUsers(Pageable pageable) {
        logger.info("Fetching all users with pagination: {}", pageable);
        Page<UserDto> userDtos = userService.fetchAllUsers(pageable);
        logger.info("Retrieved {} users successfully", userDtos.getTotalElements());

        return ResponseEntity.ok(
                new ApiResponse<>(HttpStatus.OK.value(), "Users retrieved successfully", userDtos)
        );
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<ApiResponse<Page<UserDto>>> getUsersByRole(@PathVariable String role, Pageable pageable) {
        logger.info("Fetching users by role: '{}' with pagination: {}", role, pageable);
        Page<UserDto> userDtos = userService.fetchUsersByRole(role, pageable);
        logger.info("Retrieved {} users successfully for role '{}'", userDtos.getTotalElements(), role);

        return ResponseEntity.ok(
                new ApiResponse<>(HttpStatus.OK.value(), "Users retrieved successfully for role: " + role, userDtos)
        );
    }

    @GetMapping("/sorted")
    public ResponseEntity<ApiResponse<Page<UserDto>>> getUsersSortedByAge(@RequestParam(defaultValue = "true") boolean isAscending, Pageable pageable) {
        logger.info("Fetching users sorted by age in '{}' order with pagination: {}", isAscending, pageable);
        Page<UserDto> sortedUsers = userService.fetchUsersSortedByAge(isAscending, pageable);
        logger.info("Retrieved {} sorted users successfully", sortedUsers.getTotalElements());

        return ResponseEntity.ok(
                new ApiResponse<>(HttpStatus.OK.value(), "Users sorted successfully", sortedUsers)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long id) {
        logger.info("Fetching user by ID: {}", id);
        Optional<UserDto> userDto = userService.fetchUserById(id);

        if (userDto.isPresent()) {
            logger.info("User with ID {} found", id);
            return ResponseEntity.ok(
                    new ApiResponse<>(HttpStatus.OK.value(), "User retrieved successfully", userDto.get())
            );
        } else {
            logger.warn("User with ID {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "User not found", null));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<UserDto>> getUserBySsn(@RequestParam(required = false) String ssn) {
        logger.info("Fetching user by SSN: {}", ssn);
        Optional<UserDto> userDto = userService.fetchUserBySsn(ssn);

        if (userDto.isPresent()) {
            logger.info("User with SSN {} found", ssn);
            return ResponseEntity.ok(
                    new ApiResponse<>(HttpStatus.OK.value(), "User found", userDto.get())
            );
        } else {
            logger.warn("User with SSN {} not found", ssn);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "User not found", null));
        }
    }

    @PostMapping("/load")
    public CompletableFuture<ResponseEntity<ApiResponse<Integer>>> loadUsers() {
        logger.info("Initiating user data load from external API...");
        ApiResponse<Integer> response = userLoadingService.loadUsers();

        if (response.getStatus() == 503) {
            logger.error("Circuit Breaker OPEN - External API unavailable. Returning 503.");
            return CompletableFuture.completedFuture(
                    ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response)
            );
        }

        logger.info("Users loaded successfully: {} users added.", response.getData());
        return CompletableFuture.completedFuture(ResponseEntity.ok(response));
    }
}

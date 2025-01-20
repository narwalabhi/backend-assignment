package com.narwal.assignment.service.load;

import com.narwal.assignment.dto.ApiResponse;
import com.narwal.assignment.dto.DummyUserDto;
import com.narwal.assignment.entity.User;
import com.narwal.assignment.integration.user.ExternalUserAdapter;
import com.narwal.assignment.mapper.UserMapper;
import com.narwal.assignment.service.db.UserDbService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Service
public class UserLoadingService {
    private static final Logger logger = LoggerFactory.getLogger(UserLoadingService.class);

    private final ExternalUserAdapter externalUserAdapter;
    private final UserDbService userDbService;
    private final UserMapper userMapper;

    public UserLoadingService(ExternalUserAdapter externalUserAdapter, UserDbService userDbService, UserMapper userMapper) {
        this.externalUserAdapter = externalUserAdapter;
        this.userDbService = userDbService;
        this.userMapper = userMapper;
    }

    @CircuitBreaker(name = "externalUserService", fallbackMethod = "fallbackLoadUsers")
    public ApiResponse<Integer> loadUsers() {
        try {
            logger.info("Fetching users from external API...");
            List<DummyUserDto> users = externalUserAdapter.getAllUsers();

            if (users.isEmpty()) {
                logger.warn("loadUsers() - No users found in the external source.");
                return new ApiResponse<>(200, "No users found in the external source", 0);
            }

            List<User> userEntities = users.stream().map(userMapper::toEntity).toList();
            userDbService.saveAll(userEntities);
            logger.info("✅ Users successfully loaded and saved to the database. Count: {}", userEntities.size());

            return new ApiResponse<>(200, "Users successfully loaded", userEntities.size());
        } catch (RestClientException e) {
            logger.error("RestClientException - Error fetching users from external API.", e);
            throw new RestClientException("Error fetching users from external API.", e);
        } catch (RuntimeException e) {
            logger.error("RuntimeException - Error processing user data.", e);
            throw new RuntimeException("Error processing user data.", e);
        }
    }

    //Fallback method - Called when Circuit Breaker is OPEN
    public ApiResponse<Integer> fallbackLoadUsers(RestClientException ex) {
        logger.error("Circuit Breaker activated! External API is down. Skipping user loading.", ex);
        return new ApiResponse<>(503, "Service Unavailable: Unable to fetch users at this time. Please try again later.", 0);
    }

    public ApiResponse<Integer> fallbackLoadUsers(RuntimeException ex) {
        logger.error("Circuit Breaker activated! External API is down. Skipping user loading.", ex);
        return new ApiResponse<>(500, "Service Unavailable: Unable to fetch users at this time. Please try again later.", 0);
    }
}
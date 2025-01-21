package com.narwal.assignment.integration.user.impl;

import com.narwal.assignment.dto.DummyUserApiResponse;
import com.narwal.assignment.dto.DummyUserDto;
import com.narwal.assignment.integration.user.ExternalUserAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Service
public class DummyJsonUserAdapter implements ExternalUserAdapter {

    private static final Logger logger = LoggerFactory.getLogger(DummyJsonUserAdapter.class);

    private final String baseUrl;
    private final String usersEndpoint;
    private final RestTemplate restTemplate;

    public DummyJsonUserAdapter(@Value("${dummy.api.base.url}") String baseUrl, @Value("${dummy.api.userEndpoint}") String usersEndpoint, RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
        this.usersEndpoint = usersEndpoint;
    }

    @Override
    @Retryable(
            value = {IOException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public List<DummyUserDto> getAllUsers() {
        String url = baseUrl + usersEndpoint;
        logger.info("Fetching users from external API: {}", url);

        try {
            logger.debug("Sending GET request to DummyJson API...");
            DummyUserApiResponse response = makeRequest(url);

            if (response != null && response.getUsers() != null) {
                logger.info("Successfully fetched {} users from DummyJson API.", response.getUsers().size());
                return response.getUsers();
            }
            return List.of();
        } catch (RestClientException e) {
            logger.error("Failed to fetch users from DummyJson API: {}", e.getMessage(), e);
            throw new RestClientException("Error fetching users from DummyJson API.", e);
        } catch (Exception e) {
            logger.error("Unexpected error occurred while fetching users from API.", e);
            throw new RuntimeException("Error fetching users from DummyJson API.", e);
        }
    }

    public DummyUserApiResponse makeRequest(String url) {
        return restTemplate.getForObject(url, DummyUserApiResponse.class);
    }
}
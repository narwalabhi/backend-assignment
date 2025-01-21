package com.narwal.assignment;

import com.narwal.assignment.dto.ApiResponse;
import com.narwal.assignment.dto.DummyUserDto;
import com.narwal.assignment.entity.User;
import com.narwal.assignment.integration.user.ExternalUserAdapter;
import com.narwal.assignment.mapper.UserMapper;
import com.narwal.assignment.service.db.UserDbService;
import com.narwal.assignment.service.load.UserLoadingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserLoadingServiceTest {

    @Mock
    private ExternalUserAdapter externalUserAdapter;

    @Mock
    private UserDbService userDbService;

    @Mock
    private UserMapper userMapper;

    private UserLoadingService userLoadingService;

    private DummyUserDto dummyUserDto;
    private User user;

    @BeforeEach
    void setUp() {
        userLoadingService = new UserLoadingService(externalUserAdapter, userDbService, userMapper);

        // Mock User DTO
        dummyUserDto = new DummyUserDto();
        dummyUserDto.setFirstName("John");
        dummyUserDto.setLastName("Doe");
        dummyUserDto.setAge(30);
        dummyUserDto.setEmail("john.doe@example.com");

        // Mock User Entity
        user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setAge(30);
        user.setEmail("john.doe@example.com");
    }


    @Test
    void loadUsers_ShouldFetchAndStoreUsers() {
        when(externalUserAdapter.getAllUsers()).thenReturn(Collections.singletonList(dummyUserDto));
        when(userMapper.toEntity(dummyUserDto)).thenReturn(user);

        ApiResponse<Integer> response = userLoadingService.loadUsers();

        assertEquals(200, response.getStatus());
        assertEquals(1, response.getData());
        verify(userDbService, times(1)).saveAll(anyList());
    }


    @Test
    void loadUsers_ShouldReturnZero_WhenNoUsersFetched() {
        when(externalUserAdapter.getAllUsers()).thenReturn(Collections.emptyList());

        ApiResponse<Integer> response = userLoadingService.loadUsers();

        assertEquals(200, response.getStatus());
        assertEquals(0, response.getData());
        verify(userDbService, never()).saveAll(anyList());
    }


    @Test
    void fallbackLoadUsers_ShouldReturnServiceUnavailable_WhenRestClientExceptionOccurs() {
        RestClientException exception = new RestClientException("External API failure");

        ApiResponse<Integer> response = userLoadingService.fallbackLoadUsers(exception);

        assertEquals(503, response.getStatus());
        assertEquals(0, response.getData());
        assertEquals("Service Unavailable: Unable to fetch users at this time. Please try again later.", response.getMessage());
    }


    @Test
    void fallbackLoadUsers_ShouldReturnServiceUnavailable_WhenRuntimeExceptionOccurs() {
        RuntimeException exception = new RuntimeException("Unexpected failure");

        ApiResponse<Integer> response = userLoadingService.fallbackLoadUsers(exception);

        assertEquals(500, response.getStatus());
        assertEquals(0, response.getData());
        assertEquals("Service Unavailable: Unable to fetch users at this time. Please try again later.", response.getMessage());
    }
}
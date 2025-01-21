package com.narwal.assignment;

import com.narwal.assignment.dto.DummyUserApiResponse;
import com.narwal.assignment.dto.DummyUserDto;
import com.narwal.assignment.integration.user.impl.DummyJsonUserAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
class DummyJsonUserAdapterTest {

    @Mock
    private RestTemplate restTemplate;

    private DummyJsonUserAdapter dummyJsonUserAdapter;

    private static final String BASE_URL = "https://dummyjson.com/";
    private static final String USER_ENDPOINT = "users";

    @BeforeEach
    void setUp() {
        dummyJsonUserAdapter = new DummyJsonUserAdapter(BASE_URL, USER_ENDPOINT,restTemplate);
    }

    @Test
    void getAllUsers_shouldReturnListOfUsers_whenApiResponseIsSuccessful() {
        String url = BASE_URL + USER_ENDPOINT;

        DummyUserDto user1 = new DummyUserDto();
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setEmail("john@dummy.com");

        DummyUserDto user2 = new DummyUserDto();
        user2.setFirstName("John");
        user2.setLastName("David");
        user2.setEmail("johnDavid@dummy.com");

        List<DummyUserDto> mockUsers = List.of(
                user1,
                user2
        );
        DummyUserApiResponse mockResponse = new DummyUserApiResponse();
        mockResponse.setUsers(mockUsers);

        Mockito.when(restTemplate.getForObject(String.valueOf(eq(url)), eq(DummyUserApiResponse.class))).thenReturn(mockResponse);

        List<DummyUserDto> users = dummyJsonUserAdapter.getAllUsers();

        // Assert
        Assertions.assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("John", users.get(0).getFirstName());
        assertEquals("Doe", users.get(0).getLastName());
    }

    @Test
    void getAllUsers_shouldReturnEmptyList_whenApiResponseIsNull() {
        // Arrange
        String url = BASE_URL + USER_ENDPOINT;
        Mockito.when(restTemplate.getForObject(url, DummyUserApiResponse.class)).thenReturn(null);
        List<DummyUserDto> dummyUserDtos = dummyJsonUserAdapter.getAllUsers();
        assertEquals(List.of(), dummyUserDtos);
    }

    @Test
    void getAllUsers_shouldThrowRuntimeException_whenApiResponseHasNullUsers() {
        String url = BASE_URL + USER_ENDPOINT;
        DummyUserApiResponse mockResponse = new DummyUserApiResponse();
        mockResponse.setUsers(null);

        Mockito.when(restTemplate.getForObject(url, DummyUserApiResponse.class)).thenReturn(mockResponse);

        List<DummyUserDto> dummyUserDtoList = dummyJsonUserAdapter.getAllUsers();

        assertEquals(List.of(), dummyUserDtoList);
    }

    @Test
    void getAllUsers_shouldThrowRestClientException_whenRestClientExceptionOccurs() {
        // Arrange
        String url = BASE_URL + USER_ENDPOINT;
        Mockito.when(restTemplate.getForObject(url, DummyUserApiResponse.class))
                .thenThrow(new RestClientException("API error"));

        // Act & Assert
        RestClientException exception = Assertions.assertThrows(RestClientException.class, () -> dummyJsonUserAdapter.getAllUsers());
        assertEquals("Error fetching users from DummyJson API.", exception.getMessage());
    }

    @Test
    void getAllUsers_shouldThrowRuntimeException_whenUnexpectedExceptionOccurs() {
        // Arrange
        String url = BASE_URL + USER_ENDPOINT;
        Mockito.when(restTemplate.getForObject(url, DummyUserApiResponse.class))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> dummyJsonUserAdapter.getAllUsers());
        assertEquals("Error fetching users from DummyJson API.", exception.getMessage());
    }

    @Test
    void getAllUsers_shouldThrow_whenUrlIsWrong() {
        // Arrange
        String endPoint = "us";
        String url = BASE_URL + endPoint;
        DummyJsonUserAdapter dummyJsonUserAdapter1 = new DummyJsonUserAdapter(BASE_URL, endPoint, restTemplate);
        Mockito.when(restTemplate.getForObject(url, DummyUserApiResponse.class))
                .thenThrow(new RestClientException("IO Exception"));

        // Act & Assert
        Assertions.assertThrows(RestClientException.class, () -> dummyJsonUserAdapter1.getAllUsers());
    }
}
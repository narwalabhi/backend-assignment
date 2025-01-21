package com.narwal.assignment;

import com.narwal.assignment.entity.User;
import com.narwal.assignment.repository.h2.UserRepository;
import com.narwal.assignment.service.db.impl.UserH2DbServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserH2DbServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserH2DbServiceImpl userH2DbService;

    private User user1, user2;
    private Page<User> userPage;

    @BeforeEach
    void setUp() {
        userH2DbService = new UserH2DbServiceImpl(userRepository);
        // Mock User Objects
        user1 = new User();
        user1.setId(1L);
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setRole("Admin");
        user1.setAge(30);
        user1.setSsn("123-45-6789");
        user2 = new User();
        user2.setId(2L);
        user2.setFirstName("Jack");
        user2.setLastName("Den");
        user2.setRole("User");
        user2.setAge(25);
        user2.setSsn("987-65-4321");

        // Mock Pageable
        Pageable pageable = PageRequest.of(0, 2);
        userPage = new PageImpl<>(List.of(user1, user2), pageable, 2);
    }

    // Test: getAllUsers should return paginated users
    @Test
    void getAllUsers_shouldReturnPaginatedUsers() {
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        Page<User> result = userH2DbService.getAllUsers(PageRequest.of(0, 2));

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(userRepository, times(1)).findAll(any(Pageable.class));
    }

    // Test: getUsersByRole should return users with a specific role
    @Test
    void getUsersByRole_shouldReturnUsers_whenRoleExists() {
        when(userRepository.findUsersByRole(eq("Admin"), any(Pageable.class))).thenReturn(userPage);

        Page<User> result = userH2DbService.getUsersByRole("Admin", PageRequest.of(0, 2));

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        verify(userRepository, times(1)).findUsersByRole(eq("Admin"), any(Pageable.class));
    }

    // Test: getUsersByRole should return empty when no users found
    @Test
    void getUsersByRole_shouldReturnEmpty_whenNoUsers() {
        when(userRepository.findUsersByRole(eq("NonExistentRole"), any(Pageable.class)))
                .thenReturn(Page.empty());

        Page<User> result = userH2DbService.getUsersByRole("NonExistentRole", PageRequest.of(0, 2));

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findUsersByRole(eq("NonExistentRole"), any(Pageable.class));
    }

    // Test: getUsersByOrder should return users sorted by age (Ascending)
    @Test
    void getUsersByOrder_shouldReturnUsersSortedByAgeAscending() {
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        Page<User> result = userH2DbService.getUsersByOrder(true, PageRequest.of(0, 2));

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(userRepository, times(1)).findAll(any(Pageable.class));
    }

    // Test: getUsersByOrder should return users sorted by age (Descending)
    @Test
    void getUsersByOrder_shouldReturnUsersSortedByAgeDescending() {
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        Page<User> result = userH2DbService.getUsersByOrder(false, PageRequest.of(0, 2));

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(userRepository, times(1)).findAll(any(Pageable.class));
    }

    // Test: getUserById should return user if found
    @Test
    void getUserById_shouldReturnUser_whenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        Optional<User> result = userH2DbService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
        verify(userRepository, times(1)).findById(1L);
    }

    // Test: getUserById should return empty when user not found
    @Test
    void getUserById_shouldReturnEmpty_whenNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<User> result = userH2DbService.getUserById(999L);

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(999L);
    }

    // Test: getUserBySsn should return user if found
    @Test
    void getUserBySsn_shouldReturnUser_whenExists() {
        when(userRepository.findBySsn("123-45-6789")).thenReturn(Optional.of(user1));

        Optional<User> result = userH2DbService.getUserBySsn("123-45-6789");

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
        verify(userRepository, times(1)).findBySsn("123-45-6789");
    }

    // Test: getUserBySsn should return empty when user not found
    @Test
    void getUserBySsn_shouldReturnEmpty_whenNotFound() {
        when(userRepository.findBySsn("000-00-0000")).thenReturn(Optional.empty());

        Optional<User> result = userH2DbService.getUserBySsn("000-00-0000");

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findBySsn("000-00-0000");
    }

    // Test: saveAll should save list of users
    @Test
    void saveAll_shouldSaveUsers() {
        List<User> users = List.of(user1, user2);

        when(userRepository.saveAll(anyList())).thenReturn(users);

        userH2DbService.saveAll(users);

        verify(userRepository, times(1)).saveAll(anyList());
    }

}
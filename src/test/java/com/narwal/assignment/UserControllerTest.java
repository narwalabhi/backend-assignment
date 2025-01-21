package com.narwal.assignment;

import com.narwal.assignment.controller.UserController;
import com.narwal.assignment.dto.ApiResponse;
import com.narwal.assignment.dto.UserDto;
import com.narwal.assignment.service.load.UserLoadingService;
import com.narwal.assignment.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserLoadingService userLoadingService;

    @Test
    void getAllUsers_ShouldReturnUsers() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        Page<UserDto> userPage = new PageImpl<>(Collections.singletonList(userDto));

        when(userService.fetchAllUsers(any(Pageable.class))).thenReturn(userPage);

        mockMvc.perform(get("/api/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].firstName").value("John"));
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setFirstName("Alice");

        when(userService.fetchUserById(1L)).thenReturn(Optional.of(userDto));

        mockMvc.perform(get("/api/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("Alice"));
    }

    @Test
    void getUserById_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        when(userService.fetchUserById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/user/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void loadUsers_ShouldReturn200() throws Exception {
        ApiResponse<Integer> response = new ApiResponse<>(200, "Users loaded", 5);
        when(userLoadingService.loadUsers()).thenReturn(response);

        mockMvc.perform(post("/api/user/load"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(5));
    }
}
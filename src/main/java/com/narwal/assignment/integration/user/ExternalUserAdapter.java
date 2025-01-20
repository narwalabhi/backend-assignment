package com.narwal.assignment.integration.user;

import com.narwal.assignment.dto.DummyUserDto;

import java.util.List;

public interface ExternalUserAdapter {

    List<DummyUserDto> getAllUsers();

}

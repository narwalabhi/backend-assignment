package com.narwal.assignment.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


public class DummyUserApiResponse {
    List<DummyUserDto> users;

    public List<DummyUserDto> getUsers() {
        return users;
    }

    public void setUsers(List<DummyUserDto> users) {
        this.users = users;
    }
}

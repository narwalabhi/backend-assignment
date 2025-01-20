package com.narwal.assignment.service.db;

import com.narwal.assignment.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserDbService {

    Page<User> getAllUsers(Pageable pageable); // ✅ Added Pageable

    Page<User> getUsersByRole(String role, Pageable pageable); // ✅ Added Pageable

    Page<User> getUsersByOrder(boolean isAscending, Pageable pageable); // ✅ Uses Pageable for sorting

    Optional<User> getUserById(Long id);

    Optional<User> getUserBySsn(String ssn);

    void saveAll(List<User> users);
}
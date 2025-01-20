package com.narwal.assignment.repository.h2;

import com.narwal.assignment.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findAll(Pageable pageable); // ✅ Fetch All Users Paginated

    Page<User> findAllByOrderByAgeDesc(Pageable pageable); // ✅ Sorted Descending

    Page<User> findAllByOrderByAgeAsc(Pageable pageable); // ✅ Sorted Ascending

    Page<User> findUsersByRole(String role, Pageable pageable); // ✅ Paginated Role-Based Fetch

    Optional<User> findBySsn(String ssn); // ✅ Fetch by SSN
}
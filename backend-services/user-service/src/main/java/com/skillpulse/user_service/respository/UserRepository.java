package com.skillpulse.user_service.respository;

import com.skillpulse.user_service.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<UserEntity> findByRole(String role, Pageable pageable);

    List<UserEntity> findByStatus(String status);

    Page<UserEntity> findByRoleAndStatus(String role, String status, Pageable pageable);
}

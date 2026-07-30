package com.skillpulse.assessment_service.repository;

import com.skillpulse.assessment_service.entity.AttemptDetailsView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttemptDetailsViewRepository
        extends JpaRepository<AttemptDetailsView, Long> {

    Page<AttemptDetailsView> findByUserId(Long userId, Pageable pageable);

    List<AttemptDetailsView> findByUserId(Long userId);
}
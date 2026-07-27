package com.skillpulse.assessment_service.repository;

import com.skillpulse.assessment_service.entity.AttemptAnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttemptAnswerRepository extends JpaRepository<AttemptAnswerEntity, Long> {

    List<AttemptAnswerEntity> findByAttemptId(Long attemptId);
    Optional<AttemptAnswerEntity> findByAttemptIdAndQuestionId(Long attemptId, Long questionId);
}

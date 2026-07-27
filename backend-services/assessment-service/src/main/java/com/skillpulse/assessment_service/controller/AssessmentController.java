package com.skillpulse.assessment_service.controller;

import com.skillpulse.assessment_service.model.*;
import com.skillpulse.assessment_service.service.AssessmentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assessments")
public class AssessmentController {

    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @PostMapping("/create")
    public ResponseEntity<AssessmentResponseDTO> createAssessment(@Valid @RequestBody CreateAssessmentRequestDTO request) {

        AssessmentResponseDTO assessmentResponseDTO = assessmentService.createAssessment(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(assessmentResponseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssessmentResponseDTO> getAssessmentById(@PathVariable Long id) {
        AssessmentResponseDTO assessmentResponseDTO = assessmentService.getAssessmentById(id);

        return ResponseEntity.ok(assessmentResponseDTO);
    }

    @GetMapping
    public ResponseEntity<Page<AssessmentSummaryDTO>> getPublishedAssessments(
            @RequestParam(name = "skillCategory", required = false) String skillCategory,
            @RequestParam(name = "difficulty", required = false) String difficulty,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
    ) {
        Page<AssessmentSummaryDTO> assessmentSummaryDTOPage = assessmentService.getPublishedAssessments(
                skillCategory,
                difficulty,
                page,
                size
        );

        return ResponseEntity.ok(assessmentSummaryDTOPage);
    }

    @PutMapping("/publish-assessment/{id}")
    public ResponseEntity<AssessmentResponseDTO> publishAssessment(@PathVariable Long id) {
        AssessmentResponseDTO assessmentResponseDTO = assessmentService.publishAssessment(id);

        return ResponseEntity.status(HttpStatus.OK).body(assessmentResponseDTO);
    }

    @PatchMapping("/{id}/archive")
    public ResponseEntity<AssessmentResponseDTO> archiveAssessment(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(assessmentService.archiveAssessment(id));
    }

    @PostMapping("/create-question")
    public ResponseEntity<QuestionResponseDTO> addQuestion(@Valid @RequestBody AddQuestionRequestDTO request) {

        QuestionResponseDTO questionResponseDTO = assessmentService.addQuestion(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(questionResponseDTO);
    }

    @GetMapping("/{id}/questions")
    public ResponseEntity<List<QuestionResponseDTO>> getQuestionsByAssessmentId(@PathVariable Long id) {

        List<QuestionResponseDTO> questionResponseDTOS = assessmentService.getQuestionsByAssessment(id);

        return ResponseEntity.ok(questionResponseDTOS);
    }

    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<String> deleteQuestion(@PathVariable Long questionId) {

        assessmentService.deleteQuestion(questionId);

        return ResponseEntity.status(HttpStatus.OK).body("Question delete successfully!");
    }


}

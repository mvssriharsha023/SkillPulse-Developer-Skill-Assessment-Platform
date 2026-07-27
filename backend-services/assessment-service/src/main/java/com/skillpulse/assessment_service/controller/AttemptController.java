package com.skillpulse.assessment_service.controller;

import com.skillpulse.assessment_service.model.AttemptResultDTO;
import com.skillpulse.assessment_service.model.StartAttemptResponseDTO;
import com.skillpulse.assessment_service.model.SubmitAnswerRequestDTO;
import com.skillpulse.assessment_service.model.SubmitAttemptRequestDTO;
import com.skillpulse.assessment_service.service.AttemptService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/attempts")
public class AttemptController {

    private final AttemptService attemptService;

    public AttemptController(AttemptService attemptService) {
        this.attemptService = attemptService;
    }

    @PostMapping("/start")
    public ResponseEntity<StartAttemptResponseDTO> startAttempt(
            @RequestParam(name = "assessmentId", required = true) Long assessmentId,
            @RequestParam(name = "userId", required = true) Long userId
    ) {
        StartAttemptResponseDTO startAttemptResponseDTO = attemptService.startAttempt(assessmentId, userId);

        return new ResponseEntity<>(startAttemptResponseDTO, HttpStatus.CREATED);
    }

    @PostMapping("/answer")
    public ResponseEntity<String> saveAnswer(@Valid @RequestBody SubmitAnswerRequestDTO submitAnswerRequestDTO) {
        attemptService.saveAnswer(submitAnswerRequestDTO);

        return new ResponseEntity<>("Answer saved successfully!", HttpStatus.OK);
    }

    @PostMapping("/submit")
    public ResponseEntity<AttemptResultDTO> submitAttempt(@Valid @RequestBody SubmitAttemptRequestDTO submitAttemptRequestDTO) {
        AttemptResultDTO attemptResultDTO = attemptService.submitAttempt(submitAttemptRequestDTO);

        return new ResponseEntity<>(attemptResultDTO, HttpStatus.OK);
    }

    @GetMapping("/{attemptId}")
    public ResponseEntity<AttemptResultDTO> getAttemptResult(@PathVariable Long attemptId) {

        AttemptResultDTO attemptResultDTO = attemptService.getAttemptResult(attemptId);

        return new ResponseEntity<>(attemptResultDTO, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<AttemptResultDTO>> getUserAttempts(
            @PathVariable Long userId,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {

         Page<AttemptResultDTO> resultDTOPage = attemptService.getUserAttempts(userId, page, size);

        return new ResponseEntity<>(resultDTOPage, HttpStatus.OK);
    }
}

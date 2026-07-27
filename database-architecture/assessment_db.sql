-- Active: 1783665855197@@127.0.0.1@3306@assessment_db
CREATE TABLE assessments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    skill_category VARCHAR(100) NOT NULL,
    difficulty ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED') NOT NULL,
    time_limit_minutes INT NOT NULL DEFAULT 30,
    total_questions INT NOT NULL DEFAULT 0,
    max_score INT NOT NULL DEFAULT 0,
    passing_score INT NOT NULL DEFAULT 60,
    created_by_user_id BIGINT NOT NULL,
    status ENUM('DRAFT', 'PUBLISHED', 'ARCHIVED') NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    assessment_id BIGINT NOT NULL,
    question_text TEXT NOT NULL,
    question_type ENUM('MULTIPLE_CHOICE', 'TRUE_FALSE') NOT NULL,
    option_a VARCHAR(500) NOT NULL,
    option_b VARCHAR(500) NOT NULL,
    option_c VARCHAR(500),
    option_d VARCHAR(500),
    correct_option ENUM('A', 'B', 'C', 'D') NOT NULL,
    points INT NOT NULL DEFAULT 10,
    display_order INT NOT NULL DEFAULT 1,
    CONSTRAINT fk_question_assessment
        FOREIGN KEY (assessment_id) REFERENCES assessments(id) ON DELETE CASCADE
);

CREATE TABLE assessment_attempts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    assessment_id BIGINT NOT NULL,
    status ENUM('IN_PROGRESS', 'SUBMITTED', 'TIMED_OUT', 'ABANDONED'),
    started_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    submitted_at DATETIME,
    time_taken_seconds INT,
    CONSTRAINT fk_attempt_assessment
        FOREIGN KEY (assessment_id) REFERENCES assessments(id),
    CONSTRAINT uq_user_assessment UNIQUE (user_id, assessment_id)
    -- One attempt per user per assessment
);

CREATE TABLE attempt_answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    attempt_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    selected_option ENUM('A', 'B', 'C', 'D'),
    is_correct BOOLEAN,
    CONSTRAINT fk_answer_attempt
        FOREIGN KEY (attempt_id) REFERENCES assessment_attempts(id) ON DELETE CASCADE,
    CONSTRAINT fk_answer_question
        FOREIGN KEY (question_id) REFERENCES questions(id),
    CONSTRAINT uq_attempt_question UNIQUE(attempt_id, question_id)
);

CREATE TABLE assessment_audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    assessment_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    old_status VARCHAR(50),
    new_status VARCHAR(50),
    changed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_assessments_category ON assessments(skill_category);
CREATE INDEX idx_assessments_difficulty ON assessments(difficulty, status);
CREATE INDEX idx_assessments_status ON assessments(status);
CREATE INDEX idx_questions_assessment ON questions(assessment_id);
CREATE INDEX idx_questions_order ON questions(assessment_id, display_order);
CREATE INDEX idx_attempts_user_id ON assessment_attempts(user_id);
CREATE INDEX idx_attempts_assessment_id ON assessment_attempts(assessment_id);
CREATE INDEX idx_attempts_status ON assessment_attempts(status, started_at);
CREATE INDEX idx_answers_attempt_id ON attempt_answers(attempt_id);


--Trigger to update assessments total_questions and amx_score when question in added
DELIMITER $$

CREATE TRIGGER trg_update_assessment_totals_insert
AFTER INSERT ON questions
FOR EACH ROW
BEGIN
    UPDATE assessments
    SET total_questions = total_questions + 1,
        max_score = max_score + NEW.points
    WHERE id = NEW.assessment_id;
END $$

DELIMITER ;


--Trigger to update totals when question is deleted
DELIMITER $$

CREATE TRIGGER trg_update_assessment_totals_delete
AFTER DELETE ON questions
FOR EACH ROW
BEGIN
    UPDATE assessments
    SET total_questions = total_questions - 1,
        max_score = max_score - OLD.points
    WHERE id = OLD.assessment_id;
END $$
DELIMITER ;

--Trigger - Prevent submitting an already submitted attempt
DELIMITER $$

CREATE TRIGGER trg_prevent_resubmission
BEFORE UPDATE ON assessment_attempts
FOR EACH ROW
BEGIN
    IF OLD.status = 'SUBMITTED' AND NEW.status = 'SUBMITTED' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Assessment Already Submitted. Cannot resubmit.';
    END IF;
    IF OLD.status IN ('SUBMITTED', 'TIMED_OUT') AND NEW.status = 'IN_PROGRESS' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Cannot reopen a completed assessment attempt.';
    END IF;
END $$
DELIMITER ;

--Trigger Audit log on assessment status change
DELIMITER $$

CREATE TRIGGER trg_assessment_status_audit
AFTER UPDATE ON assessments
FOR EACH ROW
BEGIN
    IF OLD.status != NEW.status THEN
        INSERT INTO assessment_audit_log (assessment_id, action, old_status, new_status)
        VALUES (NEW.id, 'STATUS_CHANGE', OLD.status, NEW.status);
    END IF;
END $$
DELIMITER ;


--Stored Procedure - Submit Assessment Attempt
DELIMITER $$
CREATE PROCEDURE SubmitAssessmentAttempt(
    IN  p_attempt_id        BIGINT,
    IN  p_time_taken_seconds INT,
    OUT p_success           BOOLEAN,
    OUT p_message           VARCHAR(200),
    OUT p_correct_count     INT,
    OUT p_total_questions   INT
)
BEGIN
    DECLARE v_attempt_status    VARCHAR(20);
    DECLARE v_correct           INT DEFAULT 0;
    DECLARE v_total             INT DEFAULT 0;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_success = FALSE;
        SET p_message = 'Unexpected error during submission. Transaction rolled back.';
    END;

    START TRANSACTION;

    -- Validate attempt exists and is IN_PROGRESS
    SELECT status INTO v_attempt_status
    FROM assessment_attempts
    WHERE id = p_attempt_id
    FOR UPDATE;

    IF v_attempt_status IS NULL THEN
        SET p_success = FALSE;
        SET p_message = 'Attempt not found';
        ROLLBACK;

    ELSEIF v_attempt_status != 'IN_PROGRESS' THEN
        SET p_success = FALSE;
        SET p_message = CONCAT('Cannot submit. Current status: ', v_attempt_status);
        ROLLBACK;

    ELSE
        -- Grade all answers
        UPDATE attempt_answers aa
        JOIN questions q ON aa.question_id = q.id
        SET aa.is_correct = (aa.selected_option = q.correct_option)
        WHERE aa.attempt_id = p_attempt_id;

        -- Count correct answers
        SELECT
            SUM(CASE WHEN is_correct = TRUE THEN 1 ELSE 0 END),
            COUNT(*)
        INTO v_correct, v_total
        FROM attempt_answers
        WHERE attempt_id = p_attempt_id;

        -- Update attempt status
        UPDATE assessment_attempts
        SET status = 'SUBMITTED',
            submitted_at = NOW(),
            time_taken_seconds = p_time_taken_seconds
        WHERE id = p_attempt_id;

        COMMIT;

        SET p_success = TRUE;
        SET p_message = 'Assessment submitted successfully';
        SET p_correct_count = COALESCE(v_correct, 0);
        SET p_total_questions = COALESCE(v_total, 0);
    END IF;
END$$
DELIMITER ;


--Views - Assessment summary (public facing, no correct answers exposed)
CREATE VIEW vw_assessment_summary AS
SELECT
    a.id,
    a.title,
    a.description,
    a.skill_category,
    a.difficulty,
    a.time_limit_minutes,
    a.total_questions,
    a.max_score,
    a.passing_score,
    a.status,
    COUNT(DISTINCT aa.user_id) AS total_attempts,
    a.created_at
FROM assessments a
LEFT JOIN assessment_attempts aa
    ON a.id = aa.assessment_id AND aa.status = 'SUBMITTED'
WHERE a.status = 'PUBLISHED'
GROUP BY a.id;

-- Views: Attempt details with scoring info
CREATE VIEW vw_attempt_details AS
SELECT
    att.id AS attempt_id,
    att.user_id,
    att.assessment_id,
    a.title AS assessment_title,
    a.skill_category,
    a.difficulty,
    a.max_score,
    a.passing_score,
    att.status,
    att.started_at,
    att.submitted_at,
    att.time_taken_seconds,
    COUNT(ans.id)                                           AS total_answered,
    a.total_questions,
    SUM(CASE WHEN ans.is_correct = TRUE THEN 1 ELSE 0 END) AS correct_answers,
    SUM(CASE WHEN ans.is_correct = TRUE
        THEN q.points ELSE 0 END)                          AS raw_score,
    ROUND(
        (SUM(CASE WHEN ans.is_correct = TRUE THEN q.points ELSE 0 END)
         / a.max_score) * 100, 2
    )                                                       AS score_percentage
FROM assessment_attempts att
JOIN assessments a ON att.assessment_id = a.id
LEFT JOIN attempt_answers ans ON att.id = ans.attempt_id
LEFT JOIN questions q ON ans.question_id = q.id
GROUP BY att.id, att.user_id, att.assessment_id, a.title,
         a.skill_category, a.difficulty, a.max_score, a.passing_score,
         att.status, att.started_at, att.submitted_at, att.time_taken_seconds;
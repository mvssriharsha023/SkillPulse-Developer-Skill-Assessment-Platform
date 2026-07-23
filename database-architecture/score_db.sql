-- Active: 1783665855197@@127.0.0.1@3306@score_db
CREATE TABLE scores (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT NOT NULL,
    assessment_id       BIGINT NOT NULL,
    attempt_id          BIGINT NOT NULL UNIQUE,
    raw_score           INT NOT NULL DEFAULT 0,
    max_score           INT NOT NULL DEFAULT 0,
    score_percentage    DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    correct_answers     INT NOT NULL DEFAULT 0,
    total_questions     INT NOT NULL DEFAULT 0,
    time_taken_seconds  INT,
    passed              BOOLEAN NOT NULL DEFAULT FALSE,
    skill_category      VARCHAR(100) NOT NULL,
    difficulty          ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED') NOT NULL,
    scored_at           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_attempt_score UNIQUE (attempt_id)
);

CREATE TABLE leaderboard_entries (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT NOT NULL UNIQUE,
    full_name           VARCHAR(100) NOT NULL,
    total_score         BIGINT NOT NULL DEFAULT 0,
    assessments_passed  INT NOT NULL DEFAULT 0,
    assessments_taken   INT NOT NULL DEFAULT 0,
    average_percentage  DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    best_category       VARCHAR(100),
    rank_position       INT,
    last_updated        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE skill_scores (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT NOT NULL,
    skill_category      VARCHAR(100) NOT NULL,
    assessments_taken   INT NOT NULL DEFAULT 0,
    assessments_passed  INT NOT NULL DEFAULT 0,
    best_score          DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    average_score       DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    last_assessed_at    DATETIME,
    CONSTRAINT uq_user_skill UNIQUE (user_id, skill_category)
);

CREATE INDEX idx_scores_user_id ON scores(user_id);
CREATE INDEX idx_scores_assessment_id ON scores(assessment_id);
CREATE INDEX idx_scores_skill_category ON scores(skill_category, score_percentage);
CREATE INDEX idx_scores_user_skill ON scores(user_id, skill_category);
CREATE INDEX idx_scores_scored_at ON scores(scored_at);
CREATE INDEX idx_leaderboard_total_score ON leaderboard_entries(total_score DESC);
CREATE INDEX idx_leaderboard_avg ON leaderboard_entries(average_percentage DESC);
CREATE INDEX idx_skill_scores_user_id ON skill_scores(user_id);
CREATE INDEX idx_skill_scores_category ON skill_scores(skill_category, average_score);


DELIMITER $$
CREATE PROCEDURE UpdateLeaderboard(
    IN p_user_id    BIGINT,
    IN p_full_name  VARCHAR(100),
    IN p_score_id   BIGINT
)
BEGIN
    DECLARE v_total_score       BIGINT DEFAULT 0;
    DECLARE v_assessments_passed INT DEFAULT 0;
    DECLARE v_assessments_taken  INT DEFAULT 0;
    DECLARE v_avg_percentage    DECIMAL(5,2) DEFAULT 0.00;
    DECLARE v_best_category     VARCHAR(100);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
    END;

    START TRANSACTION;

    -- Recalculate all stats for this user from scores table
    SELECT
        COALESCE(SUM(raw_score), 0),
        COALESCE(SUM(CASE WHEN passed = TRUE THEN 1 ELSE 0 END), 0),
        COUNT(*),
        COALESCE(AVG(score_percentage), 0.00)
    INTO v_total_score, v_assessments_passed, v_assessments_taken, v_avg_percentage
    FROM scores
    WHERE user_id = p_user_id;

    -- Find best category (most assessments passed in one category)
    SELECT skill_category INTO v_best_category
    FROM scores
    WHERE user_id = p_user_id AND passed = TRUE
    GROUP BY skill_category
    ORDER BY COUNT(*) DESC, AVG(score_percentage) DESC
    LIMIT 1;

    -- Upsert leaderboard
    INSERT INTO leaderboard_entries
        (user_id, full_name, total_score, assessments_passed,
         assessments_taken, average_percentage, best_category)
    VALUES
        (p_user_id, p_full_name, v_total_score, v_assessments_passed,
         v_assessments_taken, v_avg_percentage, v_best_category)
    ON DUPLICATE KEY UPDATE
        full_name           = p_full_name,
        total_score         = v_total_score,
        assessments_passed  = v_assessments_passed,
        assessments_taken   = v_assessments_taken,
        average_percentage  = v_avg_percentage,
        best_category       = v_best_category,
        last_updated        = NOW();

    -- Recalculate rank positions for ALL users
    SET @rank = 0;
    UPDATE leaderboard_entries
    SET rank_position = (@rank := @rank + 1)
    ORDER BY total_score DESC, average_percentage DESC;

    COMMIT;
END$$
DELIMITER ;


DELIMITER $$
CREATE PROCEDURE UpdateSkillScore(
    IN p_user_id        BIGINT,
    IN p_skill_category VARCHAR(100),
    IN p_score_pct      DECIMAL(5,2),
    IN p_passed         BOOLEAN
)
BEGIN
    INSERT INTO skill_scores
        (user_id, skill_category, assessments_taken, assessments_passed,
         best_score, average_score, last_assessed_at)
    VALUES
        (p_user_id, p_skill_category, 1,
         CASE WHEN p_passed THEN 1 ELSE 0 END,
         p_score_pct, p_score_pct, NOW())
    ON DUPLICATE KEY UPDATE
        assessments_taken   = assessments_taken + 1,
        assessments_passed  = assessments_passed + CASE WHEN p_passed THEN 1 ELSE 0 END,
        best_score          = GREATEST(best_score, p_score_pct),
        average_score       = ((average_score * (assessments_taken)) + p_score_pct)
                              / (assessments_taken + 1),
        last_assessed_at    = NOW();
END$$
DELIMITER ;

--Views for score_db
-- View 1: Top 10 leaderboard with rank
CREATE VIEW vw_top_leaderboard AS
SELECT
    rank_position,
    user_id,
    full_name,
    total_score,
    assessments_passed,
    assessments_taken,
    ROUND(average_percentage, 2) AS average_percentage,
    best_category
FROM leaderboard_entries
WHERE rank_position <= 10
ORDER BY rank_position;

-- View 2: Skill distribution — which skills are most assessed
CREATE VIEW vw_skill_distribution AS
SELECT
    skill_category,
    COUNT(*)                            AS total_attempts,
    COUNT(DISTINCT user_id)             AS unique_users,
    ROUND(AVG(score_percentage), 2)     AS avg_score,
    ROUND(MAX(score_percentage), 2)     AS top_score,
    SUM(CASE WHEN passed = TRUE THEN 1 ELSE 0 END) AS total_passed,
    ROUND(
        SUM(CASE WHEN passed = TRUE THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2
    )                                   AS pass_rate_percentage
FROM scores
GROUP BY skill_category
ORDER BY total_attempts DESC;

-- View 3: User score history with window functions
CREATE VIEW vw_user_score_history AS
SELECT
    user_id,
    skill_category,
    score_percentage,
    scored_at,
    RANK() OVER (
        PARTITION BY user_id, skill_category
        ORDER BY score_percentage DESC
    )                               AS rank_in_skill,
    ROW_NUMBER() OVER (
        PARTITION BY user_id
        ORDER BY scored_at DESC
    )                               AS attempt_recency,
    AVG(score_percentage) OVER (
        PARTITION BY user_id
        ORDER BY scored_at
        ROWS BETWEEN 4 PRECEDING AND CURRENT ROW
    )                               AS rolling_5_avg
FROM scores
ORDER BY user_id, scored_at DESC;


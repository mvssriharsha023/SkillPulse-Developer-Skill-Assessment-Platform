-- Active: 1783665855197@@127.0.0.1@3306@user_db
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('DEVELOPER', 'COMPANY') NOT NULL DEFAULT 'DEVELOPER',
    bio TEXT,
    github_url VARCHAR(255),
    status ENUM('ACTIVE', 'INACTIVE', 'BANNED') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE developer_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    experience_years INT NOT NULL DEFAULT 0,
    primary_skill VARCHAR(100),
    total_assessments INT NOT NULL DEFAULT 0,
    total_badges INT NOT NULL DEFAULT 0,
    average_score DECIMAL(5, 2) NOT NULL DEFAULT 0.00,
    rank_tier ENUM('BRONZE', 'SILVER', 'GOLD', 'PLATINUM', 'DIAMOND') NOT NULL DEFAULT 'BRONZE',
    last_active_at DATETIME,

    CONSTRAINT fk_profile_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE badges (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL,
    icon_url VARCHAR(255),
    criteria_type ENUM('SCORE_THRESHOLD', 'ASSESSMENT_COUNT', 'PERFECT_SCORE', 'STREAK') NOT NULL,
    criteria_value INT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_badges (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    badge_id BIGINT NOT NULL,
    awarded_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ub_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ub_badge
        FOREIGN KEY (badge_id) REFERENCES badges(id),
    CONSTRAINT uq_user_badge UNIQUE(user_id, badge_id)
);

CREATE TABLE user_audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    old_value VARCHAR(255),
    new_value VARCHAR(255),
    changed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE INDEX idx_users_email ON users(email);

CREATE INDEX idx_users_role_status ON users(role, status);

CREATE INDEX idx_developer_profiles_user_id ON developer_profiles(user_id);

CREATE INDEX idx_developer_profiles_rank ON developer_profiles(rank_tier, average_score);

CREATE INDEX idx_user_badges_user_id ON user_badges(user_id);

CREATE INDEX idx_user_badges_badge_id ON user_badges(badge_id);

CREATE INDEX idx_user_audit_log_user_id ON user_audit_log(user_id);


--Auto create developer profile on user insert
DELIMITER $$

CREATE TRIGGER trg_create_developer_profile
AFTER INSERT ON users
FOR EACH ROW
BEGIN
    IF NEW.role = 'DEVELOPER' THEN
        INSERT INTO developer_profiles (user_id)
        VALUES (NEW.id);
    END IF;
END $$
DELIMITER ;

--Audit log on user status change
DELIMITER $$

CREATE TRIGGER trg_user_status_audit
AFTER UPDATE ON users
FOR EACH ROW
BEGIN
    IF OLD.status != NEW.status THEN
        INSERT INTO user_audit_log (user_id, action, old_value, new_value)
        VALUES (NEW.id, 'STATUA_CHANGE', OLD.status, NEW.status);
    END IF;
    IF OLD.role != NEW.role THEN
        INSERT INTO user_audit_log (user_id, action, old_value, new_value)
        VALUES (NEW.id, 'ROLE_CHANGE', OLD.role, NEW.role);
    END IF;
END $$
DELIMITER ;

DELIMITER $$

CREATE TRIGGER trg_prevent_banned_reactivation
BEFORE UPDATE ON users
FOR EACH ROW
BEGIN
    IF OLD.status = 'BANNED' AND NEW.status = 'ACTIVE' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Cannot reactivate a banned user directly. Contact admin.';
    END IF;
END $$
DELIMITER ;

INSERT INTO badges (name, description, criteria_type, criteria_value) VALUES
('First Step',     'Completed your first assessment',          'ASSESSMENT_COUNT',  1),
('Quick Learner',  'Completed 5 assessments',                  'ASSESSMENT_COUNT',  5),
('Dedicated',      'Completed 10 assessments',                 'ASSESSMENT_COUNT',  10),
('High Scorer',    'Scored above 80% on any assessment',       'SCORE_THRESHOLD',   80),
('Excellence',     'Scored above 95% on any assessment',       'SCORE_THRESHOLD',   95),
('Perfectionist',  'Got a perfect score on any assessment',    'PERFECT_SCORE',     100),
('Silver Tier',    'Reached Silver rank tier',                 'SCORE_THRESHOLD',   70),
('Gold Tier',      'Reached Gold rank tier',                   'SCORE_THRESHOLD',   85);
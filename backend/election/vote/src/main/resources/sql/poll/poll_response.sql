CREATE TABLE poll_response
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL COMMENT '인덱싱 컬럼',
    poll_id     BIGINT                            NOT NULL COMMENT '여론조사 ID',
    account_id           BIGINT                            NULL COMMENT '응답자 ID (비로그인 시 NULL)',
    anonymous_session_id VARCHAR(255)                      NULL COMMENT '비로그인 사용자 세션 ID',
    option_id            BIGINT                            NULL COMMENT '선택지 ID (SINGLE_CHOICE, MULTIPLE_CHOICE)',
    score_value INT                               NULL COMMENT '점수 (SCORE)',
    created_at  DATETIME                          NOT NULL COMMENT '생성일',
    updated_at  DATETIME                          NULL COMMENT '수정일',
    deleted_at  DATETIME                          NULL COMMENT '삭제일',
    INDEX idx_poll_account (poll_id, account_id, deleted_at) COMMENT '응답 조회용 인덱스',
    INDEX idx_poll_session (poll_id, anonymous_session_id, deleted_at) COMMENT '비로그인 응답 조회용 인덱스',
    INDEX idx_account_id (account_id, id, deleted_at) COMMENT 'No Offset 페이지네이션용 커버링 인덱스'
);

-- No Offset 페이지네이션용 커버링 인덱스 추가
CREATE INDEX idx_account_id ON poll_response (account_id, id, deleted_at);

-- 2026-02-21 비로그인 투표 지원
ALTER TABLE poll_response
ADD COLUMN anonymous_session_id VARCHAR(255) NULL COMMENT '비로그인 사용자 세션 ID'
AFTER account_id;

ALTER TABLE poll_response
MODIFY COLUMN account_id BIGINT NULL COMMENT '응답자 ID (비로그인 시 NULL)';

ALTER TABLE poll_response
ADD INDEX idx_poll_session (poll_id, anonymous_session_id, deleted_at);

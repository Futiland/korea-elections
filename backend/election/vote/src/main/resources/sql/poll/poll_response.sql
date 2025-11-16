CREATE TABLE poll_response
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL COMMENT '인덱싱 컬럼',
    poll_id     BIGINT                            NOT NULL COMMENT '여론조사 ID',
    account_id  BIGINT                            NOT NULL COMMENT '응답자 ID',
    option_id   BIGINT                            NULL COMMENT '선택지 ID (SINGLE_CHOICE, MULTIPLE_CHOICE)',
    score_value INT                               NULL COMMENT '점수 (SCORE)',
    created_at  DATETIME                          NOT NULL COMMENT '생성일',
    updated_at  DATETIME                          NULL COMMENT '수정일',
    deleted_at  DATETIME                          NULL COMMENT '삭제일',
    INDEX idx_poll_account (poll_id, account_id, deleted_at) COMMENT '응답 조회용 인덱스',
    INDEX idx_account_id (account_id, id, deleted_at) COMMENT 'No Offset 페이지네이션용 커버링 인덱스'
);

-- No Offset 페이지네이션용 커버링 인덱스 추가
CREATE INDEX idx_account_id ON poll_response (account_id, id, deleted_at);

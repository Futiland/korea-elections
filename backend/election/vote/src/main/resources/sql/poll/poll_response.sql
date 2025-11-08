CREATE TABLE poll_response
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL COMMENT '인덱싱 컬럼',
    poll_id     BIGINT                            NOT NULL COMMENT '여론조사 ID',
    account_id  BIGINT                            NOT NULL COMMENT '응답자 ID',
    score_value INT                               NULL COMMENT '점수 (점수제 응답)',
    created_at  DATETIME                          NOT NULL COMMENT '생성일',
    updated_at  DATETIME                          NULL COMMENT '수정일',
    deleted_at  DATETIME                          NULL COMMENT '삭제일',
    UNIQUE (poll_id, account_id, deleted_at) COMMENT '중복응답 제어용 (재응답 불가 시)'
);

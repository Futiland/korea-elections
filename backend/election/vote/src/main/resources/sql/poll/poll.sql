CREATE TABLE poll
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL COMMENT '인덱싱 컬럼',
    title              VARCHAR(200)                      NOT NULL COMMENT '제목 (질문)',
    description        TEXT                              NOT NULL COMMENT '설명',
    question_type      ENUM('SINGLE_CHOICE', 'MULTIPLE_CHOICE', 'SCORE') NOT NULL COMMENT '질문 타입',
    poll_type          ENUM('SYSTEM', 'PUBLIC')          NOT NULL COMMENT '여론조사 타입',
    status             ENUM('DRAFT', 'IN_PROGRESS', 'EXPIRED', 'CANCELLED', 'DELETED') NOT NULL COMMENT '상태',
    is_revotable       BOOLEAN                           NOT NULL COMMENT '재투표 가능 여부',
    creator_account_id BIGINT                            NOT NULL COMMENT '생성자 ID',
    start_at           DATETIME                          NULL COMMENT '시작일시',
    end_at             DATETIME                          NULL COMMENT '종료일시',
    created_at         DATETIME                          NOT NULL COMMENT '생성일',
    updated_at         DATETIME                          NULL COMMENT '수정일',
    deleted_at         DATETIME                          NULL COMMENT '삭제일'
);

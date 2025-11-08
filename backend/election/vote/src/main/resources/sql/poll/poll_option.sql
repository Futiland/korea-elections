CREATE TABLE poll_option
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL COMMENT '인덱싱 컬럼',
    poll_id      BIGINT                            NOT NULL COMMENT '여론조사 ID',
    option_text  VARCHAR(200)                      NOT NULL COMMENT '선택지 내용',
    option_order INT                               NOT NULL COMMENT '선택지 순서',
    created_at   DATETIME                          NOT NULL COMMENT '생성일',
    deleted_at   DATETIME                          NULL COMMENT '삭제일'
);

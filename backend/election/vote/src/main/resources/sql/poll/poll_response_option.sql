CREATE TABLE poll_response_option
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL COMMENT '인덱싱 컬럼',
    response_id BIGINT                            NOT NULL COMMENT '응답 ID',
    option_id   BIGINT                            NOT NULL COMMENT '선택지 ID',
    created_at  DATETIME                          NOT NULL COMMENT '생성일',
    UNIQUE (response_id, option_id) COMMENT '동일 응답에서 같은 선택지 중복 방지'
);

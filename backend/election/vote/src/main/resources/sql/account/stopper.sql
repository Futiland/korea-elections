CREATE TABLE stopper
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL COMMENT '인덱싱 컬럼',
    service_target ENUM('SIGNUP') NOT NULL UNIQUE COMMENT '정지 대상',
    status         ENUM('ACTIVE', 'INACTIVE') NOT NULL COMMENT '정지 상태',
    message        VARCHAR(255) NOT NULL COMMENT '정지 사유'
);
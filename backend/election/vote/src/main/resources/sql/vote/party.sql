CREATE TABLE party
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL COMMENT '인덱싱 컬럼',
    name       VARCHAR(255) NOT NULL COMMENT '정당 이름',
    color      VARCHAR(255) NOT NULL COMMENT '정당 색상',
    status     ENUM('ACTIVE', 'INACTIVE') NOT NULL COMMENT '상태',
    created_at DATETIME     NOT NULL COMMENT '생성일',
    deleted_at DATETIME NULL COMMENT '삭제일'
);
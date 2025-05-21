CREATE TABLE candidate
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY not null  comment '인덱싱 컬럼',
    election_id BIGINT       not null comment '선거 ID',
    name        VARCHAR(100) not null comment '후보 성함',
    party       VARCHAR(100) not null comment '정당',
    number      INT(100) not null comment '후보 번호',
    description VARCHAR(100) not null comment '설명',
    status      ENUM('ACTIVE', 'INACTIVE') not null comment '상태',
    created_at  DATETIME     not null comment '생성일',
    deleted_at  DATETIME null comment '삭제일'
);

ALTER TABLE candidate
    ADD COLUMN party VARCHAR(100) NOT NULL DEFAULT '정당' COMMENT '정당' AFTER name;